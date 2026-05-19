import { useEffect, useRef, useState } from 'react'
import { FaBus, FaPaperPlane, FaRobot, FaTimes, FaTrash } from 'react-icons/fa'
import { chatbotApi } from '../../services/api'

const STARTER_MESSAGES = [
  'Which buses are available today?',
  'How do I cancel a booking?',
  'How can I download my ticket PDF?',
]

const WELCOME_MESSAGE = {
  id: 'welcome',
  role: 'assistant',
  text: 'Hi, I am the BookMyRoute assistant. Ask me about routes, schedules, fares, seats, bookings, payments, ticket PDFs, or cancellations.',
  suggestions: STARTER_MESSAGES,
}

function getBotPayload(res) {
  const payload = res.data?.data ?? res.data?.result ?? res.data
  return {
    text: payload?.reply || payload?.message || payload?.answer || 'I could not read the chatbot response. Please try again.',
    provider: payload?.provider,
    suggestions: Array.isArray(payload?.suggestions) ? payload.suggestions : [],
  }
}

export default function ChatbotWidget() {
  const [open, setOpen] = useState(false)
  const [messages, setMessages] = useState([WELCOME_MESSAGE])
  const [input, setInput] = useState('')
  const [loading, setLoading] = useState(false)
  const listRef = useRef(null)
  const inputRef = useRef(null)

  useEffect(() => {
    if (open) {
      listRef.current?.scrollTo({ top: listRef.current.scrollHeight, behavior: 'smooth' })
      inputRef.current?.focus()
    }
  }, [messages, open])

  const sendMessage = async (text = input) => {
    const clean = text.trim()
    if (!clean || loading) return

    const userMessage = { id: `user-${Date.now()}`, role: 'user', text: clean }
    const history = messages
      .filter(message => message.role === 'user' || message.role === 'assistant')
      .slice(-10)
      .map(({ role, text }) => ({ role, text }))

    setMessages(prev => [...prev, userMessage])
    setInput('')
    setLoading(true)

    try {
      const res = await chatbotApi.sendMessage({ message: clean, history })
      const botPayload = getBotPayload(res)
      setMessages(prev => [
        ...prev,
        {
          id: `assistant-${Date.now()}`,
          role: 'assistant',
          text: botPayload.text,
          provider: botPayload.provider,
          suggestions: botPayload.suggestions,
        },
      ])
    } catch {
      setMessages(prev => [
        ...prev,
        {
          id: `assistant-error-${Date.now()}`,
          role: 'assistant',
          text: 'I am having trouble reaching the assistant right now. Please try again in a moment.',
        },
      ])
    } finally {
      setLoading(false)
    }
  }

  const handleSubmit = (event) => {
    event.preventDefault()
    sendMessage()
  }

  const clearChat = () => {
    setMessages([WELCOME_MESSAGE])
    setInput('')
  }

  const lastSuggestions = messages
    .filter(message => message.role === 'assistant' && Array.isArray(message.suggestions) && message.suggestions.length)
    .at(-1)?.suggestions ?? STARTER_MESSAGES

  return (
    <div className="fixed bottom-5 right-5 z-50">
      {open && (
        <div className="mb-4 flex h-[min(640px,calc(100vh-7rem))] w-[min(380px,calc(100vw-2.5rem))] flex-col overflow-hidden rounded-lg border border-gray-200 bg-white shadow-2xl">
          <div className="flex items-center justify-between gap-3 bg-[#172033] px-4 py-3 text-white">
            <div className="flex items-center gap-3">
              <div className="flex h-9 w-9 items-center justify-center rounded-lg bg-white/10">
                <FaRobot />
              </div>
              <div>
                <p className="font-800">BookMyRoute Assistant</p>
                <p className="text-xs text-slate-300">Route and booking help</p>
              </div>
            </div>
            <div className="flex items-center gap-2">
              <button
                type="button"
                onClick={clearChat}
                className="flex h-9 w-9 items-center justify-center rounded-lg bg-white/10 transition-colors hover:bg-white/20"
                aria-label="Clear assistant chat"
                title="Clear chat"
              >
                <FaTrash />
              </button>
              <button
                type="button"
                onClick={() => setOpen(false)}
                className="flex h-9 w-9 items-center justify-center rounded-lg bg-white/10 transition-colors hover:bg-white/20"
                aria-label="Close assistant"
                title="Close assistant"
              >
                <FaTimes />
              </button>
            </div>
          </div>

          <div ref={listRef} className="flex-1 overflow-y-auto bg-[#f6f7fb] p-4">
            <div className="flex flex-col gap-3">
              {messages.map(message => (
                <div
                  key={message.id}
                  className={`flex ${message.role === 'user' ? 'justify-end' : 'justify-start'}`}
                >
                  <div className={`max-w-[82%] rounded-lg px-4 py-3 text-sm leading-6 shadow-sm ${
                    message.role === 'user'
                      ? 'bg-[#d84e55] text-white'
                      : 'border border-gray-200 bg-white text-[#172033]'
                  }`}>
                    <p className="whitespace-pre-wrap">{message.text}</p>
                    {message.role === 'assistant' && message.provider && (
                      <p className="mt-2 text-[10px] font-800 uppercase tracking-wide text-slate-400">
                        {message.provider === 'OPENAI' ? 'AI assisted' : 'Local help'}
                      </p>
                    )}
                  </div>
                </div>
              ))}

              {loading && (
                <div className="flex justify-start">
                  <div className="rounded-lg border border-gray-200 bg-white px-4 py-3 text-sm font-700 text-slate-500 shadow-sm">
                    Thinking...
                  </div>
                </div>
              )}
            </div>
          </div>

          <div className="border-t border-gray-200 bg-white p-3">
            <div className="mb-3 flex gap-2 overflow-x-auto pb-1">
              {lastSuggestions.map(starter => (
                <button
                  key={starter}
                  type="button"
                  onClick={() => sendMessage(starter)}
                  disabled={loading}
                  className="shrink-0 rounded-full border border-gray-200 bg-white px-3 py-1.5 text-xs font-700 text-slate-600 transition-colors hover:border-[#d84e55] hover:text-[#d84e55] disabled:opacity-60"
                >
                  {starter}
                </button>
              ))}
            </div>

            <form onSubmit={handleSubmit} className="flex items-end gap-2">
              <textarea
                ref={inputRef}
                value={input}
                onChange={event => setInput(event.target.value)}
                onKeyDown={event => {
                  if (event.key === 'Enter' && !event.shiftKey) {
                    event.preventDefault()
                    sendMessage()
                  }
                }}
                maxLength={1000}
                rows={1}
                placeholder="Ask about routes, fares, seats..."
                className="max-h-28 min-h-[44px] flex-1 resize-none rounded-lg border border-gray-300 px-3 py-2.5 text-sm text-[#172033] placeholder-slate-400 focus:border-[#d84e55] focus:outline-none focus:ring-4 focus:ring-[#d84e55]/10"
              />
              <button
                type="submit"
                disabled={loading || !input.trim()}
                className="flex h-11 w-11 shrink-0 items-center justify-center rounded-lg bg-[#d84e55] text-white transition-colors hover:bg-[#b93d45] disabled:opacity-60"
                aria-label="Send message"
              >
                <FaPaperPlane />
              </button>
            </form>
          </div>
        </div>
      )}

      <button
        type="button"
        onClick={() => setOpen(value => !value)}
        className="flex h-14 w-14 items-center justify-center rounded-full bg-[#d84e55] text-xl text-white shadow-xl transition-colors hover:bg-[#b93d45]"
        aria-label={open ? 'Hide assistant' : 'Open assistant'}
      >
        {open ? <FaTimes /> : <FaBus />}
      </button>
    </div>
  )
}
