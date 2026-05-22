import { useEffect, useMemo, useRef, useState } from 'react'
import toast from 'react-hot-toast'
import {
  FaChevronDown,
  FaEnvelope,
  FaExclamationCircle,
  FaHeadset,
  FaLifeRing,
  FaPaperPlane,
  FaQuestionCircle,
  FaReceipt,
  FaRegClock,
  FaRoute,
  FaTimes,
} from 'react-icons/fa'
import { supportApi } from '../../services/api'
import { useAuth } from '../../context/AuthContext'
import SupportChatPanel from './SupportChatPanel'

const faqs = [
  {
    question: 'How do I download my ticket?',
    answer: 'Go to My bookings, open the confirmed booking, and use Download ticket to save the PDF.',
  },
  {
    question: 'How can I cancel a booking?',
    answer: 'Open My bookings, select the booking, and click Cancel. Eligible bookings show refund status after cancellation.',
  },
  {
    question: 'Payment was deducted but booking failed. What next?',
    answer: 'Raise a complaint with your payment time, amount, and booking attempt details. The support team can trace it.',
  },
]

const issueCards = [
  { key: 'contact', title: 'Contact support', icon: <FaEnvelope />, text: 'Ask about routes, fares, ticket PDFs, or payment issues.' },
  { key: 'complaint', title: 'Raise complaint', icon: <FaLifeRing />, text: 'Create a support ticket for refunds, failed booking, or service issues.' },
  { key: 'cancel', title: 'Cancellation help', icon: <FaReceipt />, text: 'Get help with cancellation rules, refunds, and booking status.' },
]

const initialForm = {
  category: 'CONTACT_SUPPORT',
  subject: '',
  bookingRef: '',
  message: '',
  contactName: '',
  contactEmail: '',
  contactPhone: '',
}

function getTicketRef(response) {
  return response?.data?.data?.ticketRef || response?.data?.ticketRef
}

export default function HelpSupport({ mobile = false, onOpen }) {
  const { user } = useAuth()
  const [open, setOpen] = useState(false)
  const [activePanel, setActivePanel] = useState('home')
  const [expandedFaq, setExpandedFaq] = useState(0)
  const [submitting, setSubmitting] = useState(false)
  const [form, setForm] = useState(initialForm)
  const ticketPanelRef = useRef(null)
  const subjectRef = useRef(null)

  const seededForm = useMemo(() => ({
    ...initialForm,
    contactName: user?.name || '',
    contactEmail: user?.email || '',
    contactPhone: user?.phone || '',
  }), [user])

  const openHelp = () => {
    onOpen?.()
    setActivePanel('home')
    setForm(prev => ({
      ...seededForm,
      ...prev,
      contactName: prev.contactName || seededForm.contactName,
      contactEmail: prev.contactEmail || seededForm.contactEmail,
      contactPhone: prev.contactPhone || seededForm.contactPhone,
    }))
    setOpen(true)
  }

  useEffect(() => {
    if (!open) return undefined
    const originalOverflow = document.body.style.overflow
    document.body.style.overflow = 'hidden'
    return () => {
      document.body.style.overflow = originalOverflow
    }
  }, [open])

  const openForm = (panel) => {
    setActivePanel(panel)
    setForm(prev => ({
      ...seededForm,
      ...prev,
      category: panel === 'complaint' ? 'COMPLAINT' : panel === 'cancel' ? 'CANCELLATION_HELP' : 'CONTACT_SUPPORT',
      subject: prev.subject || (panel === 'cancel' ? 'Need help cancelling my booking' : ''),
      contactName: prev.contactName || seededForm.contactName,
      contactEmail: prev.contactEmail || seededForm.contactEmail,
      contactPhone: prev.contactPhone || seededForm.contactPhone,
    }))
    window.requestAnimationFrame(() => {
      ticketPanelRef.current?.scrollIntoView({ behavior: 'smooth', block: 'start' })
      subjectRef.current?.focus({ preventScroll: true })
    })
  }

  const handleChange = (event) => {
    const { name, value } = event.target
    setForm(prev => ({ ...prev, [name]: value }))
  }

  const submitSupportRequest = async (event) => {
    event.preventDefault()
    const payload = {
      ...form,
      subject: form.subject.trim(),
      message: form.message.trim(),
      bookingRef: form.bookingRef.trim(),
      contactName: form.contactName.trim(),
      contactEmail: form.contactEmail.trim(),
      contactPhone: form.contactPhone.trim(),
    }

    if (!payload.subject || !payload.message || !payload.contactName || !payload.contactEmail) {
      toast.error('Please fill in the required support details')
      return
    }

    if (payload.message.length < 10) {
      toast.error('Please add a little more detail so support can help')
      return
    }

    setSubmitting(true)

    try {
      const response = await supportApi.createRequest(payload)
      const ticketRef = getTicketRef(response)
      toast.success(ticketRef ? `Support ticket ${ticketRef} created` : 'Support request submitted')
      setForm(seededForm)
      setActivePanel('home')
    } catch (error) {
      toast.error(error.response?.data?.message || 'Unable to submit support request')
    } finally {
      setSubmitting(false)
    }
  }

  const formTitle = activePanel === 'complaint'
    ? 'Raise a complaint'
    : activePanel === 'cancel'
      ? 'Booking cancellation help'
      : 'Contact support'

  return (
    <>
      <button
        type="button"
        onClick={openHelp}
        className={mobile
          ? 'flex items-center gap-2 rounded-lg px-3 py-2 text-sm font-700 text-[#172033] hover:bg-slate-100'
          : 'flex items-center gap-2 rounded-lg px-3 py-2 text-sm font-700 text-slate-600 transition-colors hover:bg-slate-100 hover:text-[#172033]'}
        aria-label="Open help chatbot"
      >
        <FaHeadset className="text-[#d84e55]" />
        <span>Help</span>
      </button>

      {open && (
        <div className="fixed inset-0 z-[70] flex flex-col bg-white">
          <div className="shrink-0 border-b border-gray-200 bg-white/95 backdrop-blur">
            <div className="section-wrap flex min-h-16 items-center justify-between gap-3 py-3">
              <div className="flex min-w-0 items-center gap-3">
                <span className="flex h-10 w-10 shrink-0 items-center justify-center rounded-lg bg-[#d84e55] text-white">
                  <FaHeadset />
                </span>
                <div className="min-w-0">
                  <p className="truncate text-lg font-800 text-[#172033]">BookMyRoute Help</p>
                  <p className="hidden text-xs text-slate-500 sm:block">Chat support, FAQs, complaints, and cancellation help</p>
                </div>
              </div>
              <button
                type="button"
                onClick={() => setOpen(false)}
                className="flex h-10 w-10 items-center justify-center rounded-lg border border-gray-200 text-[#172033] transition-colors hover:border-[#d84e55] hover:text-[#d84e55]"
                aria-label="Close help"
              >
                <FaTimes />
              </button>
            </div>
          </div>

          <div className="min-h-0 flex-1 overflow-y-auto bg-[#f6f7fb]">
            <section className="bg-[#d84e55]">
              <div className="section-wrap grid gap-6 py-6 lg:min-h-[650px] lg:grid-cols-[390px_1fr] lg:items-center lg:gap-10 lg:py-10">
                <div className="lg:order-1">
                <SupportChatPanel onEscalate={openForm} />
              </div>

                <div className="text-center text-white lg:order-2">
                  <h1 className="text-3xl font-800 leading-tight md:text-5xl">BookMyRoute Help</h1>
                  <div className="mx-auto mt-6 hidden max-w-xl items-center justify-center sm:flex lg:mt-10">
                    <div className="relative h-56 w-full max-w-lg overflow-hidden rounded-lg bg-[#c93f48] md:h-64">
                      <div className="absolute left-10 top-10 hidden h-16 w-16 rounded-lg border-4 border-[#b93740] md:block" />
                      <div className="absolute right-8 top-10 rounded-lg bg-white px-5 py-3 text-left text-sm font-800 text-[#d84e55] shadow-lg md:right-12 md:top-12">
                      How can I help?
                    </div>
                      <div className="absolute bottom-0 left-1/2 flex h-44 w-52 -translate-x-1/2 flex-col items-center justify-end">
                        <div className="flex h-24 w-24 items-center justify-center rounded-full bg-[#ffd0b5] text-4xl text-[#172033] shadow-xl md:h-28 md:w-28 md:text-5xl">
                          <FaHeadset />
                        </div>
                        <div className="h-14 w-56 rounded-t-lg bg-[#172033] md:h-16 md:w-64" />
                      </div>
                      <div className="absolute bottom-5 left-6 flex items-center gap-2 rounded-lg bg-white/95 px-4 py-2 text-sm font-800 text-[#172033] shadow md:left-14">
                        <FaRegClock className="text-[#d84e55]" /> 24/7
                      </div>
                    </div>
                  </div>
                  <p className="mt-5 text-2xl font-800 md:mt-8 md:text-3xl">24/7 Customer Support</p>
                  <p className="mx-auto mt-3 max-w-2xl text-sm leading-6 text-white/85">
                    Start with chat for quick answers, or raise a ticket for issues that need human follow-up.
                  </p>
                </div>
              </div>
            </section>

            <section className="section-wrap py-8">
            <div className="grid gap-4 md:grid-cols-3">
              {issueCards.map(card => (
                <button
                  key={card.key}
                  type="button"
                  onClick={() => openForm(card.key)}
                  className="card-hover p-5 text-left"
                >
                  <span className="flex h-11 w-11 items-center justify-center rounded-lg bg-[#d84e55]/10 text-xl text-[#d84e55]">
                    {card.icon}
                  </span>
                  <p className="mt-4 font-800 text-[#172033]">{card.title}</p>
                  <p className="mt-2 text-sm leading-6 text-slate-500">{card.text}</p>
                </button>
              ))}
            </div>

            <div className="mt-6 grid gap-6 lg:grid-cols-[1fr_1fr]">
              <div className="card p-5">
                <div className="mb-4 flex items-center gap-2">
                  <FaQuestionCircle className="text-[#d84e55]" />
                  <h2 className="text-xl font-800 text-[#172033]">FAQ</h2>
                </div>
                <div className="divide-y divide-gray-200 rounded-lg border border-gray-200">
                  {faqs.map((faq, index) => (
                    <div key={faq.question}>
                      <button
                        type="button"
                        onClick={() => setExpandedFaq(expandedFaq === index ? -1 : index)}
                        className="flex w-full items-center justify-between gap-4 px-4 py-4 text-left"
                      >
                        <span className="font-800 text-[#172033]">{faq.question}</span>
                        <FaChevronDown className={`shrink-0 text-slate-400 transition-transform ${expandedFaq === index ? 'rotate-180' : ''}`} />
                      </button>
                      {expandedFaq === index && (
                        <p className="px-4 pb-4 text-sm leading-6 text-slate-600">{faq.answer}</p>
                      )}
                    </div>
                  ))}
                </div>
              </div>

              <div ref={ticketPanelRef} className="card scroll-mt-24 p-5">
                {activePanel === 'home' ? (
                  <div className="flex h-full flex-col justify-center">
                    <span className="flex h-12 w-12 items-center justify-center rounded-lg bg-[#172033] text-xl text-white">
                      <FaRoute />
                    </span>
                    <h2 className="mt-4 text-xl font-800 text-[#172033]">Recent issues</h2>
                    <p className="mt-2 text-sm leading-6 text-slate-500">
                      Select a support category above or use the chat panel to describe the issue. Ticket forms open here when needed.
                    </p>
                    <button type="button" onClick={() => openForm('complaint')} className="btn-primary mt-5 w-fit">
                      <FaExclamationCircle /> Raise complaint
                    </button>
                  </div>
                ) : (
                  <div>
                    <h2 className="text-xl font-800 text-[#172033]">{formTitle}</h2>
                    <p className="mt-1 text-sm text-slate-500">
                      We will create a support ticket and share a reference number.
                    </p>

                    <form onSubmit={submitSupportRequest} className="mt-5 grid gap-4 sm:grid-cols-2">
                      <label className="block">
                        <span className="mb-1 block text-xs font-800 uppercase tracking-wide text-slate-500">Name</span>
                        <input name="contactName" value={form.contactName} onChange={handleChange} className="input-field" required />
                      </label>
                      <label className="block">
                        <span className="mb-1 block text-xs font-800 uppercase tracking-wide text-slate-500">Email</span>
                        <input type="email" name="contactEmail" value={form.contactEmail} onChange={handleChange} className="input-field" required />
                      </label>
                      <label className="block">
                        <span className="mb-1 block text-xs font-800 uppercase tracking-wide text-slate-500">Phone</span>
                        <input name="contactPhone" value={form.contactPhone} onChange={handleChange} className="input-field" />
                      </label>
                      <label className="block">
                        <span className="mb-1 block text-xs font-800 uppercase tracking-wide text-slate-500">Booking ref</span>
                        <input name="bookingRef" value={form.bookingRef} onChange={handleChange} className="input-field" placeholder="Optional" />
                      </label>
                      <label className="block sm:col-span-2">
                        <span className="mb-1 block text-xs font-800 uppercase tracking-wide text-slate-500">Subject</span>
                        <input ref={subjectRef} name="subject" value={form.subject} onChange={handleChange} className="input-field" required maxLength={120} />
                      </label>
                      <label className="block sm:col-span-2">
                        <span className="mb-1 block text-xs font-800 uppercase tracking-wide text-slate-500">Message</span>
                        <textarea
                          name="message"
                          value={form.message}
                          onChange={handleChange}
                          className="input-field min-h-28 resize-y"
                          required
                          minLength={10}
                          maxLength={2000}
                        />
                      </label>
                      <div className="flex flex-wrap gap-3 sm:col-span-2">
                        <button type="submit" disabled={submitting} className="btn-primary">
                          <FaPaperPlane /> {submitting ? 'Submitting...' : 'Submit ticket'}
                        </button>
                        <button type="button" onClick={() => setActivePanel('home')} className="btn-outline">
                          Back
                        </button>
                      </div>
                    </form>
                  </div>
                )}
              </div>
            </div>
            </section>
          </div>
        </div>
      )}
    </>
  )
}
