import { createContext, useContext, useState, useCallback } from 'react'

const AuthContext = createContext(null)

export function AuthProvider({ children }) {
  const [user, setUser] = useState(() => {
    try {
      const u = localStorage.getItem('eatease_user')
      return u ? JSON.parse(u) : null
    } catch {
      return null
    }
  })
  const [token, setToken] = useState(() => localStorage.getItem('eatease_token'))

  const login = useCallback((authResponse) => {
    setToken(authResponse.token)
    setUser({
      userId: authResponse.userId,
      email: authResponse.email,
      name: authResponse.name,
      roles: authResponse.roles,
    })
    localStorage.setItem('eatease_token', authResponse.token)
    localStorage.setItem('eatease_user', JSON.stringify({
      userId: authResponse.userId,
      email: authResponse.email,
      name: authResponse.name,
      roles: authResponse.roles,
    }))
  }, [])

  const logout = useCallback(() => {
    setToken(null)
    setUser(null)
    localStorage.removeItem('eatease_token')
    localStorage.removeItem('eatease_user')
  }, [])

  return (
    <AuthContext.Provider value={{ user, token, login, logout }}>
      {children}
    </AuthContext.Provider>
  )
}

export function useAuth() {
  const ctx = useContext(AuthContext)
  if (!ctx) throw new Error('useAuth must be used within AuthProvider')
  return ctx
}
