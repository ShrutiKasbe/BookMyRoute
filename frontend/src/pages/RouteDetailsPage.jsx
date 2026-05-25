import { useEffect, useMemo, useState } from 'react'
import { useLocation, useNavigate, useParams } from 'react-router-dom'
import { format, parseISO } from 'date-fns'
import { FaArrowLeft, FaBusAlt, FaChevronLeft, FaChevronRight, FaRegStar, FaRoute, FaStar, FaUserCircle } from 'react-icons/fa'
import { routeApi, reviewApi } from '../services/api'

function fmtDate(dt) {
  if (!dt) return '--'
  try { return format(parseISO(dt), 'dd MMM yyyy') } catch { return dt }
}

function Stars({ value }) {
  return (
    <span className="inline-flex items-center gap-0.5 text-[#f59e0b]">
      {[1, 2, 3, 4, 5].map(item => item <= value ? <FaStar key={item} /> : <FaRegStar key={item} />)}
    </span>
  )
}

export default function RouteDetailsPage() {
  const { routeId } = useParams()
  const { state } = useLocation()
  const navigate = useNavigate()
  const [route, setRoute] = useState(state?.route || null)
  const [summary, setSummary] = useState(null)
  const [reviews, setReviews] = useState([])
  const [pageInfo, setPageInfo] = useState({ page: 0, totalPages: 0, totalElements: 0, last: true })
  const [page, setPage] = useState(0)
  const [loading, setLoading] = useState(true)

  const routeTitle = useMemo(() => {
    if (!route) return 'Route details'
    return `${route.origin} to ${route.destination}`
  }, [route])

  useEffect(() => {
    let active = true

    async function loadRoute() {
      try {
        const [{ data: routeData }, { data: summaryData }] = await Promise.all([
          routeApi.getRoute(routeId),
          reviewApi.getRouteSummary(routeId),
        ])
        if (!active) return
        setRoute(routeData?.data || state?.route || null)
        setSummary(summaryData?.data || null)
      } catch {
        if (active) setRoute(state?.route || null)
      }
    }

    loadRoute()
    return () => { active = false }
  }, [routeId])

  useEffect(() => {
    let active = true
    setLoading(true)
    reviewApi.getRouteReviews(routeId, { page, size: 5 })
      .then(({ data }) => {
        if (!active) return
        const payload = data?.data || {}
        setReviews(payload.content || [])
        setPageInfo({
          page: payload.page || 0,
          totalPages: payload.totalPages || 0,
          totalElements: payload.totalElements || 0,
          last: payload.last ?? true,
        })
      })
      .catch(() => {
        if (active) setReviews([])
      })
      .finally(() => active && setLoading(false))

    return () => { active = false }
  }, [routeId, page])

  const average = summary?.averageRating ?? route?.routeAverageRating ?? 0
  const count = summary?.reviewCount ?? route?.routeReviewCount ?? 0

  return (
    <div className="page-shell">
      <div className="border-b border-gray-200 bg-white">
        <div className="section-wrap py-6">
          <button onClick={() => navigate(-1)} className="mb-4 inline-flex items-center gap-2 text-sm font-800 text-slate-600 hover:text-[#d84e55]">
            <FaArrowLeft /> Back
          </button>

          <div className="grid gap-4 lg:grid-cols-[1fr_auto] lg:items-end">
            <div>
              <p className="mb-2 inline-flex items-center gap-2 rounded-full bg-[#d84e55]/10 px-3 py-1 text-xs font-800 uppercase text-[#d84e55]">
                <FaRoute /> Route details
              </p>
              <h1 className="text-2xl font-800 text-[#172033]">{routeTitle}</h1>
              <p className="mt-1 text-sm text-slate-500">
                {route?.distanceKm ? `${route.distanceKm} km` : 'Distance unavailable'}
                {route?.durationMins ? ` | ${Math.floor(route.durationMins / 60)}h ${route.durationMins % 60}m` : ''}
              </p>
            </div>

            <div className="rounded-lg border border-amber-200 bg-amber-50 px-5 py-4">
              <div className="flex items-center gap-3">
                <FaStar className="text-2xl text-[#f59e0b]" />
                <div>
                  <p className="text-2xl font-800 text-[#172033]">{count ? Number(average).toFixed(1) : '--'}</p>
                  <p className="text-xs font-800 uppercase text-amber-700">{count} Review{count === 1 ? '' : 's'}</p>
                </div>
              </div>
            </div>
          </div>

          {route?.busName && (
            <div className="mt-5 flex flex-wrap items-center gap-3 rounded-lg border border-gray-200 bg-slate-50 p-4">
              <span className="flex h-10 w-10 items-center justify-center rounded-lg bg-[#172033] text-white"><FaBusAlt /></span>
              <div>
                <p className="font-800 text-[#172033]">{route.busName}</p>
                <p className="text-sm text-slate-500">{route.busType} | Starting from Rs {route.baseFare}</p>
              </div>
            </div>
          )}
        </div>
      </div>

      <div className="section-wrap max-w-5xl py-8">
        <div className="mb-4 flex flex-wrap items-center justify-between gap-3">
          <div>
            <h2 className="text-xl font-800 text-[#172033]">Passenger reviews</h2>
            <p className="text-sm text-slate-500">Latest reviews appear first.</p>
          </div>
          <div className="badge">
            <FaStar className="text-[#f59e0b]" /> {count ? `${Number(average).toFixed(1)} average` : 'No ratings yet'}
          </div>
        </div>

        {loading ? (
          <div className="card p-10 text-center">
            <FaStar className="mx-auto mb-3 animate-pulse text-4xl text-[#f59e0b]" />
            <p className="text-sm font-700 text-slate-500">Loading reviews...</p>
          </div>
        ) : reviews.length === 0 ? (
          <div className="rounded-lg border border-dashed border-gray-300 bg-white p-12 text-center">
            <FaRegStar className="mx-auto mb-4 text-5xl text-slate-300" />
            <h2 className="text-xl font-800 text-[#172033]">No reviews yet</h2>
            <p className="mt-2 text-sm text-slate-500">Completed passengers will be able to rate this route here.</p>
          </div>
        ) : (
          <div className="grid gap-4">
            {reviews.map(review => (
              <article key={review.reviewId} className="card p-5">
                <div className="mb-3 flex flex-wrap items-start justify-between gap-3">
                  <div className="flex min-w-0 items-center gap-3">
                    <FaUserCircle className="shrink-0 text-3xl text-slate-300" />
                    <div className="min-w-0">
                      <p className="truncate font-800 text-[#172033]">{review.reviewerName || 'Passenger'}</p>
                      <p className="text-xs text-slate-500">{fmtDate(review.createdAt)}</p>
                    </div>
                  </div>
                  <Stars value={review.rating} />
                </div>
                <p className="text-sm leading-6 text-slate-600">
                  {review.comment || 'No written comment was added.'}
                </p>
              </article>
            ))}
          </div>
        )}

        {pageInfo.totalPages > 1 && (
          <div className="mt-6 flex items-center justify-between rounded-lg border border-gray-200 bg-white p-3">
            <button
              onClick={() => setPage(value => Math.max(value - 1, 0))}
              disabled={pageInfo.page === 0}
              className="btn-outline px-4 py-2"
            >
              <FaChevronLeft /> Previous
            </button>
            <span className="text-sm font-800 text-slate-600">
              Page {pageInfo.page + 1} of {pageInfo.totalPages}
            </span>
            <button
              onClick={() => setPage(value => value + 1)}
              disabled={pageInfo.last}
              className="btn-outline px-4 py-2"
            >
              Next <FaChevronRight />
            </button>
          </div>
        )}
      </div>
    </div>
  )
}
