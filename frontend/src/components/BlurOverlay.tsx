import { useState } from 'react'
import { LoginForm } from '../auth/LoginForm'

export function BlurOverlay({ message = '로그인하고 댓글을 확인하세요' }: { message?: string }) {
  const [showLogin, setShowLogin] = useState(false)

  return (
    <>
      <div className="blur-overlay">
        <div className="blur-overlay-content">
          <p className="blur-overlay-message">{message}</p>
          <button className="btn-primary" onClick={() => setShowLogin(true)}>
            로그인
          </button>
        </div>
      </div>
      {showLogin && <LoginForm onClose={() => setShowLogin(false)} />}
    </>
  )
}
