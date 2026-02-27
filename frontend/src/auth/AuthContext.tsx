import { createContext, useCallback, useEffect, useState, type ReactNode } from 'react'
import { apiClient, TOKEN_KEY } from '../api/client'

interface AuthUser {
  email: string
  nickname: string
}

interface AuthContextType {
  user: AuthUser | null
  isLoggedIn: boolean
  login: (email: string, password: string) => Promise<void>
  signup: (email: string, password: string, nickname: string) => Promise<void>
  logout: () => void
}

export const AuthContext = createContext<AuthContextType>({
  user: null,
  isLoggedIn: false,
  login: async () => {},
  signup: async () => {},
  logout: () => {},
})

interface AuthResponse {
  token: string
  email: string
  nickname: string
}

export function AuthProvider({ children }: { children: ReactNode }) {
  const [user, setUser] = useState<AuthUser | null>(null)

  useEffect(() => {
    const token = localStorage.getItem(TOKEN_KEY)
    const savedUser = localStorage.getItem('guest-blur-user')
    if (token && savedUser) {
      setUser(JSON.parse(savedUser))
    }
  }, [])

  const login = useCallback(async (email: string, password: string) => {
    const { data } = await apiClient.post<AuthResponse>('/auth/login', { email, password })
    localStorage.setItem(TOKEN_KEY, data.token)
    const authUser = { email: data.email, nickname: data.nickname }
    localStorage.setItem('guest-blur-user', JSON.stringify(authUser))
    setUser(authUser)
  }, [])

  const signup = useCallback(async (email: string, password: string, nickname: string) => {
    const { data } = await apiClient.post<AuthResponse>('/auth/signup', { email, password, nickname })
    localStorage.setItem(TOKEN_KEY, data.token)
    const authUser = { email: data.email, nickname: data.nickname }
    localStorage.setItem('guest-blur-user', JSON.stringify(authUser))
    setUser(authUser)
  }, [])

  const logout = useCallback(() => {
    localStorage.removeItem(TOKEN_KEY)
    localStorage.removeItem('guest-blur-user')
    setUser(null)
  }, [])

  return (
    <AuthContext.Provider value={{ user, isLoggedIn: user !== null, login, signup, logout }}>
      {children}
    </AuthContext.Provider>
  )
}
