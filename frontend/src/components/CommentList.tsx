import { useEffect, useState } from 'react'
import { apiClient } from '../api/client'
import { useAuth } from '../auth/useAuth'
import { BlurOverlay } from './BlurOverlay'
import { CommentCard, type Comment } from './CommentCard'

export function CommentList({ postId }: { postId: number }) {
  const { isLoggedIn } = useAuth()
  const [comments, setComments] = useState<Comment[]>([])
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    setLoading(true)
    apiClient
      .get<Comment[]>(`/posts/${postId}/comments`)
      .then(({ data }) => setComments(data))
      .catch(() => setComments([]))
      .finally(() => setLoading(false))
  }, [postId, isLoggedIn])

  if (loading) {
    return <div className="loading">댓글을 불러오는 중...</div>
  }

  return (
    <div className="comment-section">
      <h3 className="comment-section-title">댓글 ({comments.length})</h3>
      <div className={`comment-list ${!isLoggedIn ? 'comment-list-blurred' : ''}`}>
        {comments.map((comment) => (
          <CommentCard key={comment.id} comment={comment} />
        ))}
        {comments.length === 0 && <p className="no-comments">아직 댓글이 없습니다.</p>}
      </div>
      {!isLoggedIn && <BlurOverlay />}
    </div>
  )
}
