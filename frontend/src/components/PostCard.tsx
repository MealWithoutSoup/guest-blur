interface Post {
  id: number
  title: string
  content: string
  author: string
  createdAt: string
  obfuscated: boolean
}

export function PostCard({
  post,
  isSelected,
  onSelect,
}: {
  post: Post
  isSelected: boolean
  onSelect: (id: number) => void
}) {
  const date = new Date(post.createdAt)
  const formatted = date.toLocaleDateString('ko-KR', {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
  })

  return (
    <article
      className={`post-card ${isSelected ? 'post-card-selected' : ''}`}
      onClick={() => onSelect(post.id)}
    >
      <h2 className="post-title">{post.title}</h2>
      <p className={`post-content-preview ${post.obfuscated ? 'post-card-blurred' : ''}`}>
        {post.content.length > 150 ? post.content.slice(0, 150) + '...' : post.content}
      </p>
      <div className="post-meta">
        <span className={`post-author ${post.obfuscated ? 'post-card-blurred' : ''}`}>{post.author}</span>
        <span className="post-date">{formatted}</span>
      </div>
    </article>
  )
}

export type { Post }
