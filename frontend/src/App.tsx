import { AuthProvider } from './auth/AuthContext'
import { Layout } from './components/Layout'
import { PostList } from './components/PostList'

export function App() {
  return (
    <AuthProvider>
      <Layout>
        <PostList />
      </Layout>
    </AuthProvider>
  )
}
