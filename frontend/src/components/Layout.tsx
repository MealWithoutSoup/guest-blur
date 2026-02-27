import { useState, type ReactNode } from 'react'
import { useAuth } from '../auth/useAuth'
import { LoginForm } from '../auth/LoginForm'

export function Layout({ children }: { children: ReactNode }) {
  const { user, isLoggedIn, logout } = useAuth()
  const [showLogin, setShowLogin] = useState(false)

  return (
    <div className="layout">
      <header className="header">
        <div className="header-inner">
          <h1 className="logo">Guest Blur</h1>
          <nav className="nav">
            {isLoggedIn ? (
              <div className="user-info">
                <span className="user-nickname">{user?.nickname}</span>
                <button className="btn-outline" onClick={logout}>
                  로그아웃
                </button>
              </div>
            ) : (
              <button className="btn-primary" onClick={() => setShowLogin(true)}>
                로그인
              </button>
            )}
          </nav>
        </div>
      </header>
      <main className="main">{children}</main>
      {showLogin && <LoginForm onClose={() => setShowLogin(false)} />}
    </div>
  )
}
