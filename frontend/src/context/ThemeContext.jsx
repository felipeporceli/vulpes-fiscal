import { createContext, useContext, useState, useLayoutEffect } from 'react';

const ThemeContext = createContext();

export function ThemeProvider({ children }) {
  const [isDark, setIsDark] = useState(
    () => localStorage.getItem('vf_theme') === 'dark',
  );

  // Keep the DOM class in sync with React state
  useLayoutEffect(() => {
    document.documentElement.classList.toggle('dark', isDark);
    localStorage.setItem('vf_theme', isDark ? 'dark' : 'light');
  }, [isDark]);

  // Read DOM directly to avoid any stale-closure issue in the updater
  function toggleTheme() {
    const next = !document.documentElement.classList.contains('dark');
    document.documentElement.classList.toggle('dark', next);
    localStorage.setItem('vf_theme', next ? 'dark' : 'light');
    setIsDark(next);
  }

  return (
    <ThemeContext.Provider value={{ theme: isDark ? 'dark' : 'light', toggleTheme, isDark }}>
      {children}
    </ThemeContext.Provider>
  );
}

export const useTheme = () => useContext(ThemeContext);
