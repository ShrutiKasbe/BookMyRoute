import { useState, useEffect, useRef } from 'react'
import { useLocation, useNavigate, useSearchParams } from 'react-router-dom'
import { FaBolt, FaBusAlt, FaFilter, FaUserFriends, FaWifi } from 'react-icons/fa'
import { MdSwapHoriz } from 'react-icons/md'
import { format, parseISO } from 'date-fns'
import toast from 'react-hot-toast'
import { searchApi } from '../services/api'
import { CitySearchInput, JourneyDatePicker } from '../components/common/JourneySearchControls'


const FALLBACK_CITIES = ['Pune','Mumbai','Goa','Bangalore','Mysore','Chennai','Hyderabad','Delhi','Jaipur','Kolkata']

const TYPE_COLORS = {
  AC:           { bg:'#ecfdf5', text:'#047857' },
  SLEEPER:      { bg:'#eff6ff', text:'#1d4ed8' },
  SEMI_SLEEPER: { bg:'#fff7ed', text:'#c2410c' },
  SEATER:       { bg:'#fefce8', text:'#a16207' },
  NON_AC:       { bg:'#f1f5f9', text:'#475569' },
}

function fmtTime(dt) {
  if (!dt) return '--'
  try { return format(parseISO(dt), 'HH:mm') } catch { return dt.slice(11,16) || '--' }
}

function calcDuration(mins) {
  if (!mins) return ''
  const h = Math.floor(mins / 60)
  const m = mins % 60
  return m ? `${h}h ${m}m` : `${h}h`
}

function parseAmenities(raw) {
  if (!raw) return []
  return raw.split(',').map(s => s.trim()).filter(Boolean)
}

function BusCard({ bus, onSelect }) {
  
  const typeStyle = TYPE_COLORS[bus.busType] || TYPE_COLORS.SEATER
  const amenities = parseAmenities(bus.amenities)
  const depTime = fmtTime(bus.departureTime)
  const arrTime = fmtTime(bus.arrivalTime)
  const duration = calcDuration(bus.durationMins)
  const lowSeats = Number(bus.availableSeats) <= 5
 
  return (
    <div className="card-hover p-5">
      <div className="grid gap-5 lg:grid-cols-[1.25fr_1fr_auto] lg:items-center">
        <div className="flex gap-4">
          <div className="flex h-12 w-12 shrink-0 items-center justify-center rounded-lg bg-[#d84e55]/10 text-[#d84e55]">
            <FaBusAlt />
          </div>
          <div className="min-w-0">
            <div className="flex flex-wrap items-center gap-2">
              <h3 className="truncate text-lg font-800 text-[#172033]">{bus.busName}</h3>
              <span className="rounded-full px-2.5 py-1 text-xs font-800" style={typeStyle}>
                {bus.busType}
              </span>
            </div>
            <div className="mt-2 flex flex-wrap gap-2">
              {amenities.slice(0, 4).map(a => (
                <span key={a} className="inline-flex items-center gap-1 rounded-full bg-slate-100 px-2.5 py-1 text-xs font-700 text-slate-600">
                  {a === 'WiFi' && <FaWifi className="text-[#2563eb]" />}
                  {a === 'USB' && <FaBolt className="text-[#f59e0b]" />}
                  {a}
                </span>
              ))}
            </div>
          </div>
        </div>

        <div className="grid grid-cols-[1fr_auto_1fr] items-center gap-4">
          <div>
            <p className="text-2xl font-800 text-[#172033]">{depTime}</p>
            <p className="mt-1 text-sm text-slate-500">{bus.origin}</p>
          </div>
          <div className="text-center">
            <p className="text-xs font-700 text-slate-400">{duration || 'Direct'}</p>
            <div className="mt-1 flex w-24 items-center gap-2">
              <span className="h-px flex-1 bg-gray-300" />
              <FaBusAlt className="text-xs text-[#d84e55]" />
              <span className="h-px flex-1 bg-gray-300" />
            </div>
          </div>
          <div className="text-right">
            <p className="text-2xl font-800 text-[#172033]">{arrTime}</p>
            <p className="mt-1 text-sm text-slate-500">{bus.destination}</p>
          </div>
        </div>

        <div className="flex items-end justify-between gap-4 border-t border-gray-100 pt-4 lg:block lg:border-t-0 lg:pt-0 lg:text-right">
          <div>
            <p className="text-xs font-700 text-slate-400">Starting from</p>
            <p className="text-2xl font-800 text-[#d84e55]">Rs {bus.baseFare}</p>
           
            <p className={`mt-1 text-xs font-800 ${lowSeats ? 'text-red-600' : 'text-emerald-600'}`}>
              {bus.availableSeats} seats left
            </p>
          </div>
          <button onClick={() => onSelect(bus)} className="btn-primary whitespace-nowrap">
            Select seats
          </button>
        </div>
      </div>
    </div>
  )
}

export default function SearchPage() {
  const navigate = useNavigate()
  const { state } = useLocation()
  const [searchParams] = useSearchParams()
  const autoSearched = useRef(false)
  const [cities, setCities] = useState(FALLBACK_CITIES)
  const [form, setForm] = useState({
    from: state?.searchParams?.from || searchParams.get('origin') || 'Pune',
    to: state?.searchParams?.to || searchParams.get('destination') || 'Mumbai',
    date: state?.searchParams?.date || searchParams.get('travelDate') || new Date().toISOString().split('T')[0],
    passengers: state?.searchParams?.passengers || searchParams.get('seats') || 1,
  })
  const [results, setResults] = useState(null)
  const [loading, setLoading] = useState(false)
  const [sortBy, setSortBy] = useState('price')

  useEffect(() => {
    searchApi.getCities()
      .then(res => { if (res.data?.data?.length) setCities(res.data.data) })
      .catch(() => {})
  }, [])

  const set = (k) => (value) => setForm(f => ({ ...f, [k]: value }))
  const swap = () => setForm(f => ({ ...f, from: f.to, to: f.from }))

  const performSearch = async (params) => {
    if (params.from.trim().toLowerCase() === params.to.trim().toLowerCase()) {
      toast.error('Origin and destination cannot be the same')
      return
    }
    setLoading(true)
    try {
      const { data } = await searchApi.searchBuses({
        origin: params.from.trim(),
        destination: params.to.trim(),
        travelDate: params.date,
        seats: Number(params.passengers),
      })
      const list = data?.data ?? []
      setResults(list)
      if (list.length === 0) toast('No buses found for this route/date')
    } catch {
      setResults([])
    } finally {
      setLoading(false)
    }
  }

  const handleSearch = async (e) => {
    e.preventDefault()
    await performSearch(form)
  }

  useEffect(() => {
    const shouldAutoSearch = state?.autoSearch || searchParams.get('auto') === '1'
    if (!shouldAutoSearch || autoSearched.current) return
    autoSearched.current = true
    performSearch({
      from: state?.searchParams?.from || searchParams.get('origin') || form.from,
      to: state?.searchParams?.to || searchParams.get('destination') || form.to,
      date: state?.searchParams?.date || searchParams.get('travelDate') || form.date,
      passengers: state?.searchParams?.passengers || searchParams.get('seats') || form.passengers,
    })
  }, [])

  const sorted = results ? [...results].sort((a, b) =>
    sortBy === 'price' ? a.baseFare - b.baseFare :
    sortBy === 'departure' ? (a.departureTime || '').localeCompare(b.departureTime || '') :
    b.availableSeats - a.availableSeats
  ) : []

  return (
    <div className="page-shell">
      <div className="border-b border-gray-200 bg-white">
        <div className="section-wrap py-6">
          <h1 className="text-2xl font-800 text-[#172033]">Search buses</h1>
          <p className="mt-1 text-sm text-slate-500">Compare timings, seats and fares for your route.</p>

          <form onSubmit={handleSearch} className="mt-5 rounded-xl border border-gray-200 bg-white p-3 shadow-sm">
            <div className="grid gap-3 md:grid-cols-2 xl:grid-cols-[1fr_auto_1fr_0.85fr_0.65fr_auto] xl:items-end">
              <div>
                <CitySearchInput
                  label="From"
                  value={form.from}
                  onChange={set('from')}
                  cities={cities}
                  accent="#d84e55"
                  placeholder="Search origin city"
                />
              </div>

              <button
                type="button"
                onClick={swap}
                className="hidden h-11 w-11 items-center justify-center rounded-lg border border-gray-200 bg-gray-50 hover:border-[#d84e55] hover:text-[#d84e55] xl:flex"
                aria-label="Swap route"
              >
                <MdSwapHoriz className="text-xl" />
              </button>

              <div>
                <CitySearchInput
                  label="To"
                  value={form.to}
                  onChange={set('to')}
                  cities={cities}
                  accent="#2563eb"
                  placeholder="Search destination city"
                />
              </div>

              <div>
                <JourneyDatePicker value={form.date} onChange={set('date')} label="Date" />
              </div>

              <div>
                <label className="mb-1 flex items-center gap-2 text-xs font-800 uppercase text-slate-500">
                  <FaUserFriends className="text-[#f59e0b]" /> Riders
                </label>
                <select value={form.passengers} onChange={event => set('passengers')(event.target.value)} className="input-field">
                  {[1,2,3,4,5,6].map(n => <option key={n} value={n}>{n}</option>)}
                </select>
              </div>

              <button type="submit" disabled={loading} className="btn-primary h-12 px-7">
                {loading ? 'Searching...' : 'Search'}
              </button>
            </div>
          </form>
        </div>
      </div>

      <div className="section-wrap py-8">
        {results === null ? (
          <div className="rounded-xl border border-dashed border-gray-300 bg-white p-12 text-center">
            <FaBusAlt className="mx-auto mb-4 text-5xl text-[#d84e55]" />
            <h2 className="text-xl font-800 text-[#172033]">Start with your route</h2>
            <p className="mt-2 text-sm text-slate-500">Choose a city pair and date to see available buses.</p>
          </div>
        ) : sorted.length === 0 ? (
          <div className="rounded-xl border border-dashed border-gray-300 bg-white p-12 text-center">
            <FaBusAlt className="mx-auto mb-4 text-5xl text-slate-300" />
            <h2 className="text-xl font-800 text-[#172033]">No buses found</h2>
            <p className="mt-2 text-sm text-slate-500">Try changing the date or route.</p>
          </div>
        ) : (
          <div className="grid gap-4 lg:grid-cols-[250px_1fr]">
            <aside className="card h-fit p-5">
              <div className="mb-4 flex items-center gap-2">
                <FaFilter className="text-[#d84e55]" />
                <h2 className="font-800 text-[#172033]">Sort results</h2>
              </div>
              <div className="grid gap-2">
                {[
                  ['price', 'Lowest fare'],
                  ['departure', 'Earliest departure'],
                  ['seats', 'Most seats'],
                ].map(([key, label]) => (
                  <button
                    key={key}
                    onClick={() => setSortBy(key)}
                    className={`rounded-lg border px-3 py-2 text-left text-sm font-700 transition-colors ${
                      sortBy === key
                        ? 'border-[#d84e55] bg-[#d84e55]/10 text-[#d84e55]'
                        : 'border-gray-200 bg-white text-slate-600 hover:border-[#d84e55]/40'
                    }`}
                  >
                    {label}
                  </button>
                ))}
              </div>
            </aside>

            <main>
              <div className="mb-4 flex flex-wrap items-center justify-between gap-3">
                <div>
                  <p className="font-800 text-[#172033]">{sorted.length} bus{sorted.length > 1 ? 'es' : ''} found</p>
                  <p className="text-sm text-slate-500">{form.from} to {form.to} on {form.date}</p>
                </div>
              </div>
              <div className="flex flex-col gap-4">
                {sorted.map(bus => (
                  <BusCard
                    key={bus.scheduleId}
                    bus={bus}
                    onSelect={(b) => navigate('/book', { state: { bus: b, searchParams: form } })}
                  />
                ))}
              </div>
            </main>
          </div>
        )}
      </div>
    </div>
  )
}
