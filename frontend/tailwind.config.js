/** @type {import('tailwindcss').Config} */
export default {
  content: ['./index.html', './src/**/*.{js,jsx}'],
  theme: {
    extend: {
      fontFamily: {
        display: ['"Syne"', 'sans-serif'],
        body: ['"Plus Jakarta Sans"', 'sans-serif'],
        mono: ['"JetBrains Mono"', 'monospace'],
      },
      colors: {
        brand: {
          orange: '#FF5C00',
          yellow: '#FFD600',
          teal:   '#00C9B1',
          navy:   '#0A0F2C',
          pink:   '#FF2D78',
        }
      },
      animation: {
        'slide-up':   'slideUp 0.5s ease forwards',
        'fade-in':    'fadeIn 0.4s ease forwards',
        'pulse-slow': 'pulse 3s infinite',
        'bounce-sm':  'bounceSm 0.6s ease',
      },
      keyframes: {
        slideUp:  { from: { opacity:0, transform:'translateY(24px)' }, to: { opacity:1, transform:'translateY(0)' } },
        fadeIn:   { from: { opacity:0 }, to: { opacity:1 } },
        bounceSm: { '0%,100%': { transform:'translateY(0)' }, '50%': { transform:'translateY(-6px)' } },
      },
      fontWeight: {
        '700': '700',
        '800': '800',
      }
    }
  },
  plugins: []
}
