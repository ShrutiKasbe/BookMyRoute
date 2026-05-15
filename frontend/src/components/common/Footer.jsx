export default function Footer() {
  return (
    <footer className="border-t border-gray-200 bg-white">
      <div className="section-wrap flex flex-col gap-3 py-8 text-sm text-slate-500 md:flex-row md:items-center md:justify-between">
        <div>
          <p className="font-800 text-[#172033]">BookMyRoute</p>
          <p>Simple bus booking for everyday travel.</p>
        </div>
        <p>© {new Date().getFullYear()} BookMyRoute. All rights reserved.</p>
      </div>
    </footer>
  )
}
