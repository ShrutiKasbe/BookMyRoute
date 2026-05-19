import { useEffect, useRef, useState } from 'react'
import { FaComments, FaPaperPlane, FaRobot, FaTrash } from 'react-icons/fa'
import { chatbotApi } from '../../services/api'

const quickActions = [
  'Cancel my booking',
  'Refund status',
  'Download ticket',
  'Payment deducted',
]

const welcomeMessage = {
  id: 'welcome',
  role: 'assistant',
  text: 'Hi, I am the BookMyRoute help assistant. I can help with bookings, cancellations, refunds, ticket downloads, and complaints.',
  suggestions: quickActions,
}

function getBotPayload(res) {
  const payload = res.data?.data ?? res.data?.result ?? res.data
  return {
    text: payload?.reply || payload?.message || payload?.answer || 'I could not read the assistant response. Please try again.',
    suggestions: Array.isArray(payload?.suggestions) ? payload.suggestions : [],
  }
}

export default function SupportChatPanel({ onEscalate }) {
  const [messages, setMessages] = useState([welcomeMessage])
  const [input, setInput] = useState('')
  const [loading, setLoading] = useState(false)
  const listRef = useRef(null)
  const inputRef = useRef(null)

  useEffect(() => {
    listRef.current?.scrollTo({ top: listRef.current.scrollHeight, behavior: 'smooth' })
  }, [messages, loading])

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
          suggestions: botPayload.suggestions,
        },
      ])
    } catch {
      setMessages(prev => [
        ...prev,
        {
          id: `assistant-error-${Date.now()}`,
          role: 'assistant',
          text: 'I am having trouble reaching support right now. You can still raise a complaint and our team will follow up.',
          suggestions: ['Raise complaint', 'Contact support'],
        },
      ])
    } finally {
      setLoading(false)
      inputRef.current?.focus()
    }
  }

  const handleSuggestion = (suggestion) => {
    if (suggestion.toLowerCase().includes('complaint')) {
      onEscalate?.('complaint')
      return
    }
    if (suggestion.toLowerCase().includes('contact')) {
      onEscalate?.('contact')
      return
    }
    sendMessage(suggestion)
  }

  const clearChat = () => {
    setMessages([welcomeMessage])
    setInput('')
  }

  const suggestions = messages
    .filter(message => message.role === 'assistant' && Array.isArray(message.suggestions) && message.suggestions.length)
    .at(-1)?.suggestions ?? quickActions

  return (
    <div className="flex h-[min(620px,calc(100vh-8rem))] min-h-[520px] flex-col overflow-hidden rounded-lg border border-gray-200 bg-white shadow-xl max-sm:h-[calc(100vh-8.5rem)] max-sm:min-h-[480px]">
      <div className="flex items-center justify-between gap-3 border-b border-gray-200 px-4 py-4">
        <div className="flex min-w-0 items-center gap-3">
          <span className="flex h-10 w-10 shrink-0 items-center justify-center rounded-lg bg-[#d84e55] text-white">
            <FaRobot />
          </span>
          <div className="min-w-0">
            <p className="truncate font-800 text-[#172033]">BookMyRoute Help</p>
            <p className="text-xs text-emerald-600">Online support assistant</p>
          </div>
        </div>
        <button
          type="button"
          onClick={clearChat}
          className="flex h-9 w-9 items-center justify-center rounded-lg border border-gray-200 text-slate-500 transition-colors hover:border-[#d84e55] hover:text-[#d84e55]"
          aria-label="Clear help chat"
          title="Clear chat"
        >
          <FaTrash />
        </button>
      </div>

      <div className="border-b border-gray-100 px-4 py-3">
        <p className="mb-2 text-xs font-800 uppercase tracking-wide text-slate-500">Need help with this trip?</p>
        <div className="grid grid-cols-2 gap-2 max-[360px]:grid-cols-1">
          {quickActions.map(action => (
            <button
              key={action}
              type="button"
              onClick={() => handleSuggestion(action)}
              className="rounded-lg border border-gray-200 bg-slate-50 px-3 py-2 text-left text-xs font-800 text-[#172033] transition-colors hover:border-[#d84e55] hover:bg-[#d84e55]/5 hover:text-[#d84e55]"
            >
              {action}
            </button>
          ))}
        </div>
      </div>

      <div ref={listRef} className="min-h-0 flex-1 overflow-y-auto bg-[#f6f7fb] p-4">
        <div className="flex flex-col gap-3">
          {messages.map(message => (
            <div key={message.id} className={`flex ${message.role === 'user' ? 'justify-end' : 'justify-start'}`}>
              <div className={`max-w-[84%] break-words rounded-lg px-4 py-3 text-sm leading-6 shadow-sm ${
                message.role === 'user'
                  ? 'bg-[#d84e55] text-white'
                  : 'border border-gray-200 bg-white text-[#172033]'
              }`}>
                <p className="whitespace-pre-wrap">{message.text}</p>
              </div>
            </div>
          ))}
          {loading && (
            <div className="flex justify-start">
              <div className="rounded-lg border border-gray-200 bg-white px-4 py-3 text-sm font-700 text-slate-500 shadow-sm">
                Checking that for you...
              </div>
            </div>
          )}
        </div>
      </div>

      <div className="shrink-0 border-t border-gray-200 bg-white p-3">
        <div className="mb-3 flex gap-2 overflow-x-auto pb-1">
          {suggestions.map(suggestion => (
            <button
              key={suggestion}
              type="button"
              onClick={() => handleSuggestion(suggestion)}
              disabled={loading}
              className="shrink-0 rounded-full border border-gray-200 bg-white px-3 py-1.5 text-xs font-700 text-slate-600 transition-colors hover:border-[#d84e55] hover:text-[#d84e55] disabled:opacity-60"
            >
              {suggestion}
            </button>
          ))}
          <button
            type="button"
            onClick={() => onEscalate?.('complaint')}
            className="shrink-0 rounded-full bg-[#172033] px-3 py-1.5 text-xs font-700 text-white transition-colors hover:bg-[#22304a]"
          >
            Raise complaint
          </button>
        </div>

        <form
          onSubmit={(event) => {
            event.preventDefault()
            sendMessage()
          }}
          className="flex items-end gap-2"
        >
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
            placeholder="Type your issue..."
            className="max-h-28 min-h-[44px] flex-1 resize-none rounded-lg border border-gray-300 px-3 py-2.5 text-sm text-[#172033] placeholder-slate-400 focus:border-[#d84e55] focus:outline-none focus:ring-4 focus:ring-[#d84e55]/10"
          />
          <button
            type="submit"
            disabled={loading || !input.trim()}
            className="flex h-11 w-11 shrink-0 items-center justify-center rounded-lg bg-[#d84e55] text-white transition-colors hover:bg-[#b93d45] disabled:opacity-60"
            aria-label="Send help message"
          >
            <FaPaperPlane />
          </button>
        </form>
      </div>

      <div className="flex shrink-0 items-center gap-2 border-t border-gray-100 bg-slate-50 px-4 py-2 text-xs text-slate-500">
        <FaComments className="text-[#d84e55]" />
        Chat support can hand off to a support ticket when needed.
      </div>
    </div>
  )
}
