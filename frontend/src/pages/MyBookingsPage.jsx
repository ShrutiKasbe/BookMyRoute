import { useEffect, useState } from 'react'
import { format, parseISO } from 'date-fns'
import toast from 'react-hot-toast'
import { FaBus, FaCalendarAlt, FaDownload, FaFilter, FaMoneyBillWave, FaRoute, FaTimes, FaTicketAlt, FaUserFriends } from 'react-icons/fa'
import { bookingApi } from '../services/api'
import { useAuth } from '../context/AuthContext'

const STATUS_STYLE = {
  CONFIRMED: { bg: '#059669', label: 'Confirmed' },
  PENDING: { bg: '#f59e0b', label: 'Pending' },
  CANCELLED: { bg: '#dc2626', label: 'Cancelled' },
  COMPLETED: { bg: '#172033', label: 'Completed' },
}

function fmtDT(dt) {
  if (!dt) return '--'
  try { return format(parseISO(dt), 'dd MMM yyyy, HH:mm') } catch { return dt }
}

function statusFor(status) {
  return STATUS_STYLE[status] || STATUS_STYLE.PENDING
}

function TicketModal({ booking, onClose, onCancel, onDownload, downloading }) {
  const style = statusFor(booking.bookingStatus)
  const canCancel = booking.bookingStatus === 'CONFIRMED' || booking.bookingStatus === 'PENDING'
  const [cancelling, setCancelling] = useState(false)

  const handleCancel = async () => {
    setCancelling(true)
    try {
      await onCancel(booking.bookingRef)
      onClose()
    } finally {
      setCancelling(false)
    }
  }

  return (
    <div
      className="fixed inset-0 z-50 flex items-center justify-center bg-[#172033]/70 p-4 backdrop-blur-sm"
      onClick={event => event.target === event.currentTarget && onClose()}
    >
      <div className="w-full max-w-lg overflow-hidden rounded-lg border border-gray-200 bg-white shadow-xl">
        <div className="flex items-center justify-between bg-[#172033] px-5 py-4 text-white">
          <div className="flex items-center gap-2">
            <FaBus className="text-[#f59e0b]" />
            <span className="font-800">BookMyRoute ticket</span>
          </div>
          <button
            onClick={onClose}
            className="flex h-9 w-9 items-center justify-center rounded-lg bg-white/10 hover:bg-white/20"
            aria-label="Close ticket"
          >
            <FaTimes />
          </button>
        </div>

        <div className="p-6">
          <div className="mb-5 flex items-start justify-between gap-4">
            <div>
              <p className="text-xs font-800 uppercase text-slate-400">Booking ref</p>
              <p className="mt-1 font-mono text-sm font-800 text-[#172033]">{booking.bookingRef}</p>
            </div>
            <span className="rounded-full px-3 py-1 text-xs font-800 text-white" style={{ background: style.bg }}>
              {style.label}
            </span>
          </div>

          <div className="mb-5 rounded-lg border border-gray-200 bg-slate-50 p-4">
            <div className="grid grid-cols-[1fr_auto_1fr] items-center gap-3">
              <div>
                <p className="text-xs font-800 uppercase text-slate-400">From</p>
                <p className="truncate text-2xl font-800 text-[#172033]">{booking.origin}</p>
              </div>
              <FaBus className="text-xl text-[#d84e55]" />
              <div className="text-right">
                <p className="text-xs font-800 uppercase text-slate-400">To</p>
                <p className="truncate text-2xl font-800 text-[#172033]">{booking.destination}</p>
              </div>
            </div>
          </div>

          <div className="mb-5 grid grid-cols-2 gap-3">
            {[
              ['Bus', booking.busName],
              ['Departure', fmtDT(booking.departureTime)],
              ['Seats', booking.seats?.map(seat => seat.seatNumber).join(', ') || '--'],
              ['Passengers', booking.seats?.length || '--'],
              ['Payment', booking.paymentMethod || '--'],
              ['Pay status', booking.paymentStatus || '--'],
            ].map(([label, value]) => (
              <div key={label} className="rounded-lg bg-slate-50 p-3">
                <p className="text-xs font-800 uppercase text-slate-400">{label}</p>
                <p className="mt-1 text-sm font-800 text-[#172033]">{value}</p>
              </div>
            ))}
          </div>

          <div className="mb-5 flex items-center justify-between rounded-lg border border-amber-300 bg-amber-50 px-4 py-3">
            <span className="font-800 text-[#172033]">Total amount</span>
            <span className="text-2xl font-800 text-[#d84e55]">Rs {booking.totalAmount}</span>
          </div>

          <div className="flex flex-col gap-3 sm:flex-row">
            <button
              onClick={() => onDownload(booking.bookingRef)}
              disabled={downloading}
              className="flex flex-1 items-center justify-center gap-2 rounded-lg bg-[#172033] py-3 text-sm font-800 text-white transition-colors hover:bg-[#22304a] disabled:opacity-60"
            >
              <FaDownload />
              {downloading ? 'Downloading...' : 'Download ticket'}
            </button>

            {canCancel && (
              <button
                onClick={handleCancel}
                disabled={cancelling}
                className="flex flex-1 items-center justify-center gap-2 rounded-lg border border-red-600 py-3 text-sm font-800 text-red-600 transition-colors hover:bg-red-50 disabled:opacity-60"
              >
                <FaTimes />
                {cancelling ? 'Cancelling...' : 'Cancel booking'}
              </button>
            )}
          </div>
        </div>
      </div>
    </div>
  )
}

function BookingCard({ booking, onClick, onCancel, onDownload, downloading, cancelling }) {
  const style = statusFor(booking.bookingStatus)
  const seats = booking.seats?.length || 0
  const canCancel = booking.bookingStatus === 'CONFIRMED' || booking.bookingStatus === 'PENDING'

  return (
    <div className="card-hover w-full p-5">
      <div className="grid gap-4 lg:grid-cols-[1fr_auto_auto] lg:items-center">
        <button className="flex min-w-0 items-center gap-3 text-left" onClick={onClick}>
          <div className="flex h-11 w-11 shrink-0 items-center justify-center rounded-lg text-white" style={{ background: style.bg }}>
            <FaTicketAlt />
          </div>
          <div className="min-w-0">
            <p className="font-mono text-sm font-800 text-slate-500">{booking.bookingRef}</p>
            <p className="truncate text-lg font-800 text-[#172033]">{booking.origin} to {booking.destination}</p>
            <p className="truncate text-sm text-slate-500">{booking.busName} | {fmtDT(booking.departureTime)}</p>
          </div>
        </button>

        <div className="flex flex-wrap gap-2 text-sm text-slate-600">
          <span className="badge"><FaUserFriends /> {seats} seat{seats !== 1 ? 's' : ''}</span>
          <span className="badge"><FaMoneyBillWave /> Rs {booking.totalAmount}</span>
        </div>

        <span className="w-fit rounded-full px-3 py-1 text-xs font-800 text-white lg:justify-self-end" style={{ background: style.bg }}>
          {style.label}
        </span>
      </div>

      <div className="mt-4 flex flex-wrap justify-end gap-2 border-t border-gray-100 pt-4">
        <button
          onClick={() => onDownload(booking.bookingRef)}
          disabled={downloading}
          className="flex items-center gap-2 rounded-lg bg-[#172033] px-4 py-2 text-sm font-800 text-white transition-colors hover:bg-[#22304a] disabled:opacity-60"
        >
          <FaDownload />
          {downloading ? 'Downloading...' : 'Download ticket'}
        </button>

        {canCancel && (
          <button
            onClick={() => onCancel(booking.bookingRef)}
            disabled={cancelling}
            className="flex items-center gap-2 rounded-lg border border-red-600 px-4 py-2 text-sm font-800 text-red-600 transition-colors hover:bg-red-50 disabled:opacity-60"
          >
            <FaTimes />
            {cancelling ? 'Cancelling...' : 'Cancel booking'}
          </button>
        )}
      </div>
    </div>
  )
}

export default function MyBookingsPage() {
  const { user } = useAuth()
  const [bookings, setBookings] = useState([])
  const [loading, setLoading] = useState(true)
  const [selected, setSelected] = useState(null)
  const [filter, setFilter] = useState('ALL')
  const [downloadingRef, setDownloadingRef] = useState(null)
  const [cancellingRef, setCancellingRef] = useState(null)

  const fetchBookings = async () => {
    setLoading(true)
    try {
      const { data } = await bookingApi.getMyBookings()
      setBookings(data?.data ?? [])
    } catch {
      setBookings([])
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => { fetchBookings() }, [])

  const handleCancel = async (ref) => {
    setCancellingRef(ref)
    try {
      await bookingApi.cancelBooking(ref)
      toast.success('Booking cancelled. Refund will be processed in 3-5 days.')
      await fetchBookings()
    } finally {
      setCancellingRef(null)
    }
  }

  const handleDownloadTicket = async (ref) => {
    setDownloadingRef(ref)
    try {
      const response = await bookingApi.downloadTicket(ref)
      const blob = new Blob([response.data], { type: 'application/pdf' })
      const url = window.URL.createObjectURL(blob)
      const link = document.createElement('a')
      link.href = url
      link.download = `BookMyRoute-${ref}.pdf`
      document.body.appendChild(link)
      link.click()
      link.remove()
      window.URL.revokeObjectURL(url)
    } finally {
      setDownloadingRef(null)
    }
  }

  const filtered = filter === 'ALL' ? bookings : bookings.filter(booking => booking.bookingStatus === filter)

  return (
    <div className="page-shell">
      <div className="border-b border-gray-200 bg-white">
        <div className="section-wrap py-6">
          <h1 className="text-2xl font-800 text-[#172033]">My bookings</h1>
          <p className="mt-1 text-sm text-slate-500">Manage upcoming and past trips{user?.name ? ` for ${user.name.split(' ')[0]}` : ''}.</p>
        </div>
      </div>

      <div className="section-wrap max-w-5xl py-8">
        <div className="mb-6 grid gap-3 md:grid-cols-3">
          <div className="card p-4">
            <FaTicketAlt className="mb-3 text-[#d84e55]" />
            <p className="text-2xl font-800 text-[#172033]">{bookings.length}</p>
            <p className="text-sm text-slate-500">Total bookings</p>
          </div>
          <div className="card p-4">
            <FaCalendarAlt className="mb-3 text-[#2563eb]" />
            <p className="text-2xl font-800 text-[#172033]">{bookings.filter(b => b.bookingStatus === 'CONFIRMED').length}</p>
            <p className="text-sm text-slate-500">Confirmed trips</p>
          </div>
          <div className="card p-4">
            <FaRoute className="mb-3 text-[#059669]" />
            <p className="text-2xl font-800 text-[#172033]">{new Set(bookings.map(b => `${b.origin}-${b.destination}`)).size}</p>
            <p className="text-sm text-slate-500">Routes booked</p>
          </div>
        </div>

        <div className="mb-6 flex flex-wrap items-center gap-2">
          <FaFilter className="mr-1 text-[#d84e55]" />
          {['ALL', 'CONFIRMED', 'PENDING', 'COMPLETED', 'CANCELLED'].map(item => {
            const style = STATUS_STYLE[item]
            return (
              <button
                key={item}
                onClick={() => setFilter(item)}
                className={`rounded-lg border px-4 py-2 text-sm font-800 transition-colors ${
                  filter === item
                    ? 'border-[#172033] bg-[#172033] text-white'
                    : 'border-gray-200 bg-white text-slate-600 hover:border-[#172033]'
                }`}
              >
                {style ? style.label : 'All'}
              </button>
            )
          })}
        </div>

        {loading ? (
          <div className="card p-12 text-center">
            <FaTicketAlt className="mx-auto mb-4 animate-pulse text-5xl text-[#d84e55]" />
            <p className="text-sm font-700 text-slate-500">Loading your bookings...</p>
          </div>
        ) : filtered.length === 0 ? (
          <div className="rounded-lg border border-dashed border-gray-300 bg-white p-12 text-center">
            <FaBus className="mx-auto mb-4 text-5xl text-slate-300" />
            <h2 className="text-xl font-800 text-[#172033]">
              {filter === 'ALL' ? 'No bookings yet' : `No ${filter.toLowerCase()} bookings`}
            </h2>
            <p className="mt-2 text-sm text-slate-500">Search for a route to start your next trip.</p>
          </div>
        ) : (
          <div className="grid gap-4">
            {filtered.map(booking => (
              <BookingCard
                key={booking.bookingRef}
                booking={booking}
                onClick={() => setSelected(booking)}
                onCancel={handleCancel}
                onDownload={handleDownloadTicket}
                downloading={downloadingRef === booking.bookingRef}
                cancelling={cancellingRef === booking.bookingRef}
              />
            ))}
          </div>
        )}
      </div>

      {selected && (
        <TicketModal
          booking={selected}
          onClose={() => setSelected(null)}
          onCancel={handleCancel}
          onDownload={handleDownloadTicket}
          downloading={downloadingRef === selected.bookingRef}
        />
      )}
    </div>
  )
}
