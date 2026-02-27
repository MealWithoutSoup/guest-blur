import { useEffect, useState } from 'react'
import { apiClient } from '../api/client'
import { useAuth } from '../auth/useAuth'
import { BlurOverlay } from './BlurOverlay'
import { CommentList } from './CommentList'
import { PostCard, type Post } from './PostCard'

export function PostList() {
  const { isLoggedIn } = useAuth()
  const [posts, setPosts] = useState<Post[]>([])
  const [selectedPostId, setSelectedPostId] = useState<number | null>(null)
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    setLoading(true)
    apiClient
      .get<Post[]>('/posts')
      .then(({ data }) => {
        setPosts(data)
        if (data.length > 0 && selectedPostId === null) {
          setSelectedPostId(data[0].id)
        }
      })
      .catch(() => setPosts([]))
      .finally(() => setLoading(false))
  }, [isLoggedIn, selectedPostId])

  const handleSelect = (id: number) => {
    setSelectedPostId(id === selectedPostId ? null : id)
  }

  if (loading) {
    return <div className="loading">게시글을 불러오는 중...</div>
  }

  const selectedPost = posts.find((p) => p.id === selectedPostId)

  return (
    <div className="post-list-container">
      <div className="post-list">
        {posts.map((post) => (
          <PostCard
            key={post.id}
            post={post}
            isSelected={post.id === selectedPostId}
            onSelect={handleSelect}
          />
        ))}
      </div>
      {selectedPost && (
        <div className="post-detail">
          <h2 className="post-detail-title">{selectedPost.title}</h2>
          <div className="post-detail-meta">
            <span>{selectedPost.author}</span>
            <span>
              {new Date(selectedPost.createdAt).toLocaleDateString('ko-KR', {
                year: 'numeric',
                month: 'long',
                day: 'numeric',
              })}
            </span>
          </div>
          <div className="post-body-section">
            <div className={`post-detail-content ${!isLoggedIn ? 'post-content-blurred' : ''}`}>
              {selectedPost.content}
            </div>
            {!isLoggedIn && <BlurOverlay message="로그인하고 본문을 확인하세요" />}
          </div>
          <CommentList postId={selectedPost.id} />
        </div>
      )}
    </div>
  )
}
