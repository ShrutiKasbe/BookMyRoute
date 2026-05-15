import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { FaBusAlt, FaCalendarAlt, FaHeadset, FaMapMarkerAlt, FaShieldAlt, FaStar, FaTag } from 'react-icons/fa'
import { MdSwapHoriz } from 'react-icons/md'
import { useAuth } from '../context/AuthContext'

const CITIES = ['Pune','Mumbai','Goa','Bangalore','Mysore','Chennai','Hyderabad','Delhi','Jaipur','Kolkata']

const SERVICES = [
  { icon: <FaShieldAlt />, title: 'Secure booking', desc: 'Protected checkout and verified operators' },
  { icon: <FaBusAlt />, title: 'Live inventory', desc: 'Compare buses, seats and fares in one place' },
  { icon: <FaHeadset />, title: 'Trip support', desc: 'Help with cancellations and booking updates' },
]

const POPULAR = [
  { from: 'Pune', to: 'Mumbai', price: 350, duration: '3h' },
  { from: 'Mumbai', to: 'Goa', price: 650, duration: '8h' },
  { from: 'Bangalore', to: 'Mysore', price: 250, duration: '3h' },
  { from: 'Chennai', to: 'Coimbatore', price: 450, duration: '5h' },
]

const OFFERS = [
  { title: 'First booking offer', desc: 'Save up to Rs 500 on selected routes', code: 'BMRFIRST' },
  { title: 'Weekend saver', desc: 'Lower fares on popular Friday trips', code: 'WEEKEND' },
  { title: 'UPI cashback', desc: 'Instant cashback on eligible UPI payments', code: 'UPI100' },
]

export default function HomePage() {
  const { user } = useAuth()
  const navigate = useNavigate()
  const [form, setForm] = useState({
    from: 'Pune',
    to: 'Mumbai',
    date: new Date().toISOString().split('T')[0],
  })

  const set = (key) => (e) => setForm(f => ({ ...f, [key]: e.target.value }))
  const swap = () => setForm(f => ({ ...f, from: f.to, to: f.from }))

  const handleSearch = (e) => {
    e.preventDefault()
    navigate(user ? '/search' : '/login', { state: { searchParams: form } })
  }

  return (
    <div className="page-shell">
      <section
        className="relative overflow-hidden bg-[#172033] text-white"
        style={{
          backgroundImage:
            'linear-gradient(90deg, rgba(23,32,51,0.94), rgba(23,32,51,0.74)), url(https://images.unsplash.com/photo-1544620347-c4fd4a3d5957?auto=format&fit=crop&w=1800&q=80)',
          backgroundSize: 'cover',
          backgroundPosition: 'center',
        }}
      >
        <div className="section-wrap py-16 lg:py-20">
          <div className="max-w-3xl">
            <div className="mb-5 inline-flex items-center gap-2 rounded-full border border-white/20 bg-white/10 px-4 py-2 text-sm font-700 text-white">
              <FaStar className="text-[#f59e0b]" /> Trusted intercity bus booking
            </div>
            <h1 className="max-w-2xl text-4xl font-800 leading-tight md:text-6xl">
              Book bus tickets with clarity and confidence.
            </h1>
            <p className="mt-5 max-w-xl text-base leading-7 text-slate-200 md:text-lg">
              Search routes, compare fares, pick seats and manage bookings from one clean dashboard.
            </p>
          </div>

          <form onSubmit={handleSearch} className="mt-10 rounded-xl bg-white p-3 shadow-2xl">
            <div className="grid gap-3 lg:grid-cols-[1fr_auto_1fr_0.8fr_auto] lg:items-end">
              <div>
                <label className="mb-1 flex items-center gap-2 text-xs font-800 uppercase tracking-wide text-slate-500">
                  <FaMapMarkerAlt className="text-[#d84e55]" /> Leaving from
                </label>
                <select value={form.from} onChange={set('from')} className="input-field">
                  {CITIES.map(city => <option key={city}>{city}</option>)}
                </select>
              </div>

              <button
                type="button"
                onClick={swap}
                className="hidden h-11 w-11 items-center justify-center rounded-lg border border-gray-200 bg-gray-50 text-[#172033] hover:border-[#d84e55] hover:text-[#d84e55] lg:flex"
                aria-label="Swap origin and destination"
              >
                <MdSwapHoriz className="text-xl" />
              </button>

              <div>
                <label className="mb-1 flex items-center gap-2 text-xs font-800 uppercase tracking-wide text-slate-500">
                  <FaMapMarkerAlt className="text-[#2563eb]" /> Going to
                </label>
                <select value={form.to} onChange={set('to')} className="input-field">
                  {CITIES.map(city => <option key={city}>{city}</option>)}
                </select>
              </div>

              <div>
                <label className="mb-1 flex items-center gap-2 text-xs font-800 uppercase tracking-wide text-slate-500">
                  <FaCalendarAlt className="text-[#059669]" /> Journey date
                </label>
                <input
                  type="date"
                  value={form.date}
                  min={new Date().toISOString().split('T')[0]}
                  onChange={set('date')}
                  className="input-field"
                />
              </div>

              <button type="submit" className="btn-primary h-12 px-8 text-base">
                Search buses
              </button>
            </div>
          </form>

          <div className="mt-4 flex flex-wrap gap-2 text-sm">
            {['Today', 'Tomorrow', 'Weekend'].map(label => (
              <button key={label} className="rounded-full border border-white/20 bg-white/10 px-4 py-2 font-700 text-white hover:bg-white/20">
                {label}
              </button>
            ))}
          </div>
        </div>
      </section>

      <section className="section-wrap -mt-7 relative z-10">
        <div className="grid gap-4 rounded-xl border border-gray-200 bg-white p-4 shadow-lg md:grid-cols-3">
          {SERVICES.map(({ icon, title, desc }) => (
            <div key={title} className="flex gap-3 p-3">
              <div className="flex h-11 w-11 shrink-0 items-center justify-center rounded-lg bg-[#d84e55]/10 text-[#d84e55]">
                {icon}
              </div>
              <div>
                <p className="font-800 text-[#172033]">{title}</p>
                <p className="mt-1 text-sm leading-6 text-slate-500">{desc}</p>
              </div>
            </div>
          ))}
        </div>
      </section>

      <section className="section-wrap py-12">
        <div className="mb-5 flex items-end justify-between gap-4">
          <div>
            <h2 className="text-2xl font-800 text-[#172033]">Offers for your next trip</h2>
            <p className="mt-1 text-sm text-slate-500">Simple deals, easy to scan before booking.</p>
          </div>
        </div>
        <div className="grid gap-4 md:grid-cols-3">
          {OFFERS.map(offer => (
            <div key={offer.code} className="card-hover p-5">
              <div className="mb-4 flex h-10 w-10 items-center justify-center rounded-lg bg-[#f59e0b]/10 text-[#f59e0b]">
                <FaTag />
              </div>
              <h3 className="font-800 text-[#172033]">{offer.title}</h3>
              <p className="mt-2 text-sm leading-6 text-slate-500">{offer.desc}</p>
              <p className="mt-4 inline-flex rounded-md bg-slate-100 px-3 py-1 font-mono text-xs font-800 text-slate-700">
                {offer.code}
              </p>
            </div>
          ))}
        </div>
      </section>

      <section className="section-wrap pb-16">
        <div className="mb-5 flex items-end justify-between gap-4">
          <div>
            <h2 className="text-2xl font-800 text-[#172033]">Popular routes</h2>
            <p className="mt-1 text-sm text-slate-500">Frequently booked city pairs.</p>
          </div>
          <Link to={user ? '/search' : '/login'} className="btn-outline hidden sm:inline-flex">View all routes</Link>
        </div>

        <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-4">
          {POPULAR.map(route => (
            <Link to={user ? '/search' : '/login'} key={`${route.from}-${route.to}`} className="card-hover block p-5">
              <div className="flex items-center justify-between gap-3">
                <div className="min-w-0">
                  <p className="text-xs font-800 uppercase text-slate-400">From</p>
                  <p className="truncate text-lg font-800 text-[#172033]">{route.from}</p>
                </div>
                <FaBusAlt className="shrink-0 text-[#d84e55]" />
                <div className="min-w-0 text-right">
                  <p className="text-xs font-800 uppercase text-slate-400">To</p>
                  <p className="truncate text-lg font-800 text-[#172033]">{route.to}</p>
                </div>
              </div>
              <div className="mt-4 flex items-center justify-between border-t border-gray-100 pt-4 text-sm">
                <span className="text-slate-500">{route.duration}</span>
                <span className="font-800 text-[#d84e55]">from Rs {route.price}</span>
              </div>
            </Link>
          ))}
        </div>
      </section>
    </div>
  )
}
