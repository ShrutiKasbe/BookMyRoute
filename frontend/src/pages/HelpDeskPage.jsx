import { useMemo, useState } from 'react'
import toast from 'react-hot-toast'
import {
  FaCheckCircle,
  FaEnvelope,
  FaExclamationCircle,
  FaLifeRing,
  FaPaperPlane,
  FaPhoneAlt,
  FaReceipt,
  FaRoute,
  FaUser,
} from 'react-icons/fa'
import { supportApi } from '../services/api'
import { useAuth } from '../context/AuthContext'

const CATEGORY_OPTIONS = [
  {
    key: 'COMPLAINT',
    title: 'Raise a complaint',
    icon: <FaLifeRing />,
    description: 'Payment, failed booking, bus issue, refund delay, or anything that needs follow-up.',
    defaultSubject: '',
  },
  {
    key: 'CANCELLATION_HELP',
    title: 'Cancellation help',
    icon: <FaReceipt />,
    description: 'Questions about cancelling a trip, refund status, or cancellation rules.',
    defaultSubject: 'Need help cancelling my booking',
  },
  {
    key: 'CONTACT_SUPPORT',
    title: 'General support',
    icon: <FaEnvelope />,
    description: 'Ask about routes, tickets, fares, account details, or booking updates.',
    defaultSubject: '',
  },
]

const ISSUE_HINTS = [
  'Payment deducted but ticket not booked',
  'Need refund or cancellation status',
  'Ticket PDF or email not received',
  'Wrong passenger, route, or travel details',
  'Bus service, timing, or boarding issue',
  'Other issue',
]

const initialForm = {
  category: 'COMPLAINT',
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

export default function HelpDeskPage() {
  const { user } = useAuth()
  const [form, setForm] = useState({
    ...initialForm,
    contactName: user?.name || '',
    contactEmail: user?.email || '',
    contactPhone: user?.phone || '',
  })
  const [submitting, setSubmitting] = useState(false)
  const [submittedTicket, setSubmittedTicket] = useState('')

  const selectedCategory = useMemo(
    () => CATEGORY_OPTIONS.find(option => option.key === form.category) || CATEGORY_OPTIONS[0],
    [form.category]
  )

  const handleChange = (event) => {
    const { name, value } = event.target
    setForm(prev => ({ ...prev, [name]: value }))
    setSubmittedTicket('')
  }

  const chooseCategory = (category) => {
    setForm(prev => ({
      ...prev,
      category: category.key,
      subject: prev.subject || category.defaultSubject,
    }))
    setSubmittedTicket('')
  }

  const useHint = (hint) => {
    setForm(prev => ({
      ...prev,
      subject: hint,
      message: `${hint}\n\nBooking ref:\nWhat happened:\nExpected resolution:`,
    }))
    setSubmittedTicket('')
  }

  const resetForm = () => {
    setForm({
      ...initialForm,
      contactName: user?.name || '',
      contactEmail: user?.email || '',
      contactPhone: user?.phone || '',
    })
    setSubmittedTicket('')
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

    if (!payload.contactName || !payload.contactEmail || !payload.subject || !payload.message) {
      toast.error('Please fill in your contact details, subject, and issue description')
      return
    }

    if (payload.message.length < 10) {
      toast.error('Please describe the issue in at least 10 characters')
      return
    }

    setSubmitting(true)
    try {
      const response = await supportApi.createRequest(payload)
      const ticketRef = getTicketRef(response)
      setSubmittedTicket(ticketRef || 'submitted')
      toast.success(ticketRef ? `Support ticket ${ticketRef} created` : 'Support request submitted')
      setForm(prev => ({
        ...initialForm,
        contactName: prev.contactName,
        contactEmail: prev.contactEmail,
        contactPhone: prev.contactPhone,
      }))
    } catch (error) {
      toast.error(error.response?.data?.message || 'Unable to submit support request')
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <div className="page-shell">
      <div className="border-b border-gray-200 bg-white">
        <div className="section-wrap py-6">
          <p className="text-sm font-700 text-[#d84e55]">Support</p>
          <h1 className="mt-1 text-2xl font-800 text-[#172033]">Help desk</h1>
          <p className="mt-2 max-w-3xl text-sm leading-6 text-slate-500">
            Raise a complaint or request help with bookings, payments, cancellations, refunds, tickets, and route issues.
          </p>
        </div>
      </div>

      <div className="section-wrap max-w-6xl py-8">
        {submittedTicket && (
          <div className="mb-6 flex flex-col gap-3 rounded-lg border border-emerald-200 bg-emerald-50 p-4 text-emerald-900 sm:flex-row sm:items-center sm:justify-between">
            <div className="flex items-start gap-3">
              <FaCheckCircle className="mt-1 shrink-0 text-emerald-600" />
              <div>
                <p className="font-800">Your support request was submitted.</p>
                {submittedTicket !== 'submitted' && (
                  <p className="mt-1 font-mono text-sm">Reference: {submittedTicket}</p>
                )}
              </div>
            </div>
            <button type="button" onClick={resetForm} className="btn-outline bg-white px-4 py-2">
              Raise another issue
            </button>
          </div>
        )}

        <div className="grid gap-6 lg:grid-cols-[0.85fr_1.15fr]">
          <aside className="space-y-6">
            <section className="card p-5">
              <h2 className="text-lg font-800 text-[#172033]">What do you need help with?</h2>
              <div className="mt-4 grid gap-3">
                {CATEGORY_OPTIONS.map(option => (
                  <button
                    key={option.key}
                    type="button"
                    onClick={() => chooseCategory(option)}
                    className={`flex gap-3 rounded-lg border p-4 text-left transition-colors ${
                      form.category === option.key
                        ? 'border-[#d84e55] bg-[#d84e55]/5'
                        : 'border-gray-200 bg-white hover:border-[#d84e55]/50'
                    }`}
                  >
                    <span className="flex h-10 w-10 shrink-0 items-center justify-center rounded-lg bg-[#d84e55]/10 text-[#d84e55]">
                      {option.icon}
                    </span>
                    <span>
                      <span className="block font-800 text-[#172033]">{option.title}</span>
                      <span className="mt-1 block text-sm leading-5 text-slate-500">{option.description}</span>
                    </span>
                  </button>
                ))}
              </div>
            </section>

            <section className="card p-5">
              <h2 className="flex items-center gap-2 text-lg font-800 text-[#172033]">
                <FaExclamationCircle className="text-[#d84e55]" /> Common issues
              </h2>
              <div className="mt-4 flex flex-wrap gap-2">
                {ISSUE_HINTS.map(hint => (
                  <button
                    key={hint}
                    type="button"
                    onClick={() => useHint(hint)}
                    className="rounded-lg border border-gray-200 bg-white px-3 py-2 text-left text-xs font-800 text-slate-600 transition-colors hover:border-[#d84e55] hover:text-[#d84e55]"
                  >
                    {hint}
                  </button>
                ))}
              </div>
            </section>
          </aside>

          <section className="card p-5 sm:p-6">
            <div className="mb-6 flex flex-col gap-3 border-b border-gray-100 pb-5 sm:flex-row sm:items-start sm:justify-between">
              <div>
                <h2 className="text-xl font-800 text-[#172033]">{selectedCategory.title}</h2>
                <p className="mt-1 text-sm leading-6 text-slate-500">
                  Share enough detail for the support team to identify the issue and follow up.
                </p>
              </div>
              <span className="flex w-fit items-center gap-2 rounded-lg bg-[#172033] px-3 py-2 text-xs font-800 text-white">
                <FaRoute /> 24/7 help
              </span>
            </div>

            <form onSubmit={submitSupportRequest} className="grid gap-4 sm:grid-cols-2">
              <label className="block">
                <span className="mb-1 flex items-center gap-2 text-xs font-800 uppercase tracking-wide text-slate-500">
                  <FaUser /> Name
                </span>
                <input
                  name="contactName"
                  value={form.contactName}
                  onChange={handleChange}
                  className="input-field"
                  autoComplete="name"
                  required
                />
              </label>

              <label className="block">
                <span className="mb-1 flex items-center gap-2 text-xs font-800 uppercase tracking-wide text-slate-500">
                  <FaEnvelope /> Email
                </span>
                <input
                  type="email"
                  name="contactEmail"
                  value={form.contactEmail}
                  onChange={handleChange}
                  className="input-field"
                  autoComplete="email"
                  required
                />
              </label>

              <label className="block">
                <span className="mb-1 flex items-center gap-2 text-xs font-800 uppercase tracking-wide text-slate-500">
                  <FaPhoneAlt /> Phone
                </span>
                <input
                  name="contactPhone"
                  value={form.contactPhone}
                  onChange={handleChange}
                  className="input-field"
                  autoComplete="tel"
                  maxLength={15}
                />
              </label>

              <label className="block">
                <span className="mb-1 block text-xs font-800 uppercase tracking-wide text-slate-500">
                  Booking reference
                </span>
                <input
                  name="bookingRef"
                  value={form.bookingRef}
                  onChange={handleChange}
                  className="input-field"
                  maxLength={25}
                  placeholder="Optional"
                />
              </label>

              <label className="block sm:col-span-2">
                <span className="mb-1 block text-xs font-800 uppercase tracking-wide text-slate-500">
                  Subject
                </span>
                <input
                  name="subject"
                  value={form.subject}
                  onChange={handleChange}
                  className="input-field"
                  maxLength={120}
                  placeholder="Example: Payment deducted but booking failed"
                  required
                />
              </label>

              <label className="block sm:col-span-2">
                <span className="mb-1 block text-xs font-800 uppercase tracking-wide text-slate-500">
                  Describe the issue
                </span>
                <textarea
                  name="message"
                  value={form.message}
                  onChange={handleChange}
                  className="input-field min-h-44 resize-y"
                  minLength={10}
                  maxLength={2000}
                  placeholder="Tell us what happened, when it happened, and what you need fixed."
                  required
                />
              </label>

              <div className="flex flex-wrap gap-3 sm:col-span-2">
                <button type="submit" disabled={submitting} className="btn-primary">
                  <FaPaperPlane /> {submitting ? 'Submitting...' : 'Submit help request'}
                </button>
                <button type="button" onClick={resetForm} disabled={submitting} className="btn-outline">
                  Clear form
                </button>
              </div>
            </form>
          </section>
        </div>
      </div>
    </div>
  )
}
