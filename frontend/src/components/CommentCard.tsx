interface Comment {
  id: number
  content: string
  author: string
  createdAt: string
  obfuscated: boolean
}

export function CommentCard({ comment }: { comment: Comment }) {
  const date = new Date(comment.createdAt)
  const formatted = date.toLocaleDateString('ko-KR', {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
  })

  return (
    <div className="comment-card">
      <div className="comment-header">
        <span className="comment-author">{comment.author}</span>
        <span className="comment-date">{formatted}</span>
      </div>
      <p className="comment-content">{comment.content}</p>
    </div>
  )
}

export type { Comment }
