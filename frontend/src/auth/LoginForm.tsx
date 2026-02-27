import { useState, type FormEvent } from 'react'
import { useAuth } from './useAuth'

type Mode = 'login' | 'signup'

export function LoginForm({ onClose }: { onClose: () => void }) {
  const { login, signup } = useAuth()
  const [mode, setMode] = useState<Mode>('login')
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [nickname, setNickname] = useState('')
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault()
    setError('')
    setLoading(true)
    try {
      if (mode === 'login') {
        await login(email, password)
      } else {
        await signup(email, password, nickname)
      }
      onClose()
    } catch {
      setError(mode === 'login' ? '로그인에 실패했습니다.' : '회원가입에 실패했습니다.')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="login-modal-backdrop" onClick={onClose}>
      <div className="login-modal" onClick={(e) => e.stopPropagation()}>
        <button className="login-modal-close" onClick={onClose}>
          &times;
        </button>
        <h2>{mode === 'login' ? '로그인' : '회원가입'}</h2>
        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label htmlFor="email">이메일</label>
            <input
              id="email"
              type="email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              placeholder="email@example.com"
              required
            />
          </div>
          <div className="form-group">
            <label htmlFor="password">비밀번호</label>
            <input
              id="password"
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              placeholder="비밀번호"
              required
              minLength={4}
            />
          </div>
          {mode === 'signup' && (
            <div className="form-group">
              <label htmlFor="nickname">닉네임</label>
              <input
                id="nickname"
                type="text"
                value={nickname}
                onChange={(e) => setNickname(e.target.value)}
                placeholder="닉네임"
                required
                minLength={2}
                maxLength={20}
              />
            </div>
          )}
          {error && <p className="form-error">{error}</p>}
          <button type="submit" className="btn-primary" disabled={loading}>
            {loading ? '처리 중...' : mode === 'login' ? '로그인' : '회원가입'}
          </button>
        </form>
        <p className="form-toggle">
          {mode === 'login' ? (
            <>
              계정이 없으신가요?{' '}
              <button type="button" onClick={() => setMode('signup')}>
                회원가입
              </button>
            </>
          ) : (
            <>
              이미 계정이 있으신가요?{' '}
              <button type="button" onClick={() => setMode('login')}>
                로그인
              </button>
            </>
          )}
        </p>
      </div>
    </div>
  )
}
