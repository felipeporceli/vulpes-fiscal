/** @type {import('tailwindcss').Config} */
export default {
  content: ['./index.html', './src/**/*.{js,jsx,ts,tsx}'],
  theme: {
    extend: {
      colors: {
        vulpes: {
          dark:         '#032A47',   // navy escuro da logo
          medium:       '#08315B',   // navy médio da logo
          navy:         '#021E33',   // navy mais profundo (gradientes)
          orange:       '#FE600C',   // laranja exato da logo
          'orange-hover': '#E5520A', // laranja hover
          accent:       '#FE600C',   // alias para laranja (CTAs)
          muted:        '#6B92B0',   // azul-acinzentado para texto secundário
        },
      },
      fontFamily: {
        sans: ['Inter', 'system-ui', 'sans-serif'],
      },
      animation: {
        'fade-in':    'fadeIn 0.6s ease-out',
        'slide-up':   'slideUp 0.6s ease-out',
        'float':      'float 6s ease-in-out infinite',
        'pulse-slow': 'pulse 4s cubic-bezier(0.4, 0, 0.6, 1) infinite',
      },
      keyframes: {
        fadeIn:  { '0%': { opacity: '0' }, '100%': { opacity: '1' } },
        slideUp: {
          '0%':   { opacity: '0', transform: 'translateY(20px)' },
          '100%': { opacity: '1', transform: 'translateY(0)' },
        },
        float: {
          '0%, 100%': { transform: 'translateY(0px)' },
          '50%':      { transform: 'translateY(-12px)' },
        },
      },
    },
  },
  plugins: [],
};
