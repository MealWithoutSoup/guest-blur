import { test, expect } from '@playwright/test'

test.describe('Guest Blur - Guest View', () => {
  test('shows post list on main page', async ({ page }) => {
    await page.goto('/')
    await expect(page.locator('.post-card')).toHaveCount(3, { timeout: 10000 })
    await expect(page.locator('.logo')).toHaveText('Guest Blur')
  })

  test('shows login button for guest', async ({ page }) => {
    await page.goto('/')
    await expect(page.locator('.header .btn-primary')).toHaveText('로그인')
  })

  test('shows blur overlay on post content for guest', async ({ page }) => {
    await page.goto('/')
    await page.locator('.post-card').first().click()
    await expect(page.locator('.post-body-section .blur-overlay')).toBeVisible({ timeout: 10000 })
    await expect(page.locator('.post-body-section .blur-overlay-message')).toHaveText('로그인하고 본문을 확인하세요')
  })

  test('shows blur overlay on comments for guest', async ({ page }) => {
    await page.goto('/')
    await page.locator('.post-card').first().click()
    await expect(page.locator('.comment-section .blur-overlay')).toBeVisible({ timeout: 10000 })
    await expect(page.locator('.comment-section .blur-overlay-message')).toHaveText('로그인하고 댓글을 확인하세요')
  })

  test('post content and comments are blurred for guest', async ({ page }) => {
    await page.goto('/')
    await page.locator('.post-card').first().click()
    await expect(page.locator('.post-content-blurred')).toBeVisible({ timeout: 10000 })
    await expect(page.locator('.comment-list-blurred')).toBeVisible({ timeout: 10000 })
  })

  test('network response contains obfuscated data for guest', async ({ page }) => {
    const commentsPromise = page.waitForResponse(
      (response) => response.url().includes('/api/posts/') && response.url().includes('/comments')
    )
    await page.goto('/')
    await page.locator('.post-card').first().click()
    const commentsResponse = await commentsPromise
    const comments = await commentsResponse.json()

    expect(comments.length).toBeGreaterThan(0)
    expect(comments[0].obfuscated).toBe(true)
  })
})

test.describe('Guest Blur - Login Flow', () => {
  test('opens login modal from header', async ({ page }) => {
    await page.goto('/')
    await page.locator('.header .btn-primary').click()
    await expect(page.locator('.login-modal')).toBeVisible()
    await expect(page.locator('.login-modal h2')).toHaveText('로그인')
  })

  test('opens login modal from blur overlay', async ({ page }) => {
    await page.goto('/')
    await page.locator('.post-card').first().click()
    await page.locator('.post-body-section .blur-overlay .btn-primary').click({ timeout: 10000 })
    await expect(page.locator('.login-modal')).toBeVisible()
  })

  test('can switch between login and signup', async ({ page }) => {
    await page.goto('/')
    await page.locator('.header .btn-primary').click()
    await expect(page.locator('.login-modal h2')).toHaveText('로그인')

    await page.locator('.form-toggle button').click()
    await expect(page.locator('.login-modal h2')).toHaveText('회원가입')

    await page.locator('.form-toggle button').click()
    await expect(page.locator('.login-modal h2')).toHaveText('로그인')
  })

  test('can close login modal by clicking backdrop', async ({ page }) => {
    await page.goto('/')
    await page.locator('.header .btn-primary').click()
    await expect(page.locator('.login-modal')).toBeVisible()

    await page.locator('.login-modal-backdrop').click({ position: { x: 10, y: 10 } })
    await expect(page.locator('.login-modal')).not.toBeVisible()
  })

  test('login with valid credentials shows user nickname', async ({ page }) => {
    await page.goto('/')
    await page.locator('.header .btn-primary').click()

    await page.fill('#email', 'alice@example.com')
    await page.fill('#password', 'password')
    await page.locator('.login-modal .btn-primary').click()

    await expect(page.locator('.user-nickname')).toHaveText('Alice Kim', { timeout: 5000 })
    await expect(page.locator('.btn-outline')).toHaveText('로그아웃')
  })
})

test.describe('Guest Blur - Authenticated View', () => {
  test.beforeEach(async ({ page }) => {
    await page.goto('/')
    await page.locator('.header .btn-primary').click()
    await page.fill('#email', 'alice@example.com')
    await page.fill('#password', 'password')
    await page.locator('.login-modal .btn-primary').click()
    await expect(page.locator('.user-nickname')).toBeVisible({ timeout: 5000 })
  })

  test('post content and comments are not blurred after login', async ({ page }) => {
    await page.locator('.post-card').first().click()
    await expect(page.locator('.post-content-blurred')).not.toBeVisible({ timeout: 5000 })
    await expect(page.locator('.comment-list-blurred')).not.toBeVisible({ timeout: 5000 })
    await expect(page.locator('.blur-overlay')).not.toBeVisible()
  })

  test('network response contains original data after login', async ({ page }) => {
    const commentsPromise = page.waitForResponse(
      (response) => response.url().includes('/api/posts/') && response.url().includes('/comments')
    )
    await page.locator('.post-card').first().click()
    const commentsResponse = await commentsPromise
    const comments = await commentsResponse.json()

    expect(comments.length).toBeGreaterThan(0)
    expect(comments[0].obfuscated).toBe(false)
    expect(comments[0].author).toMatch(/^[A-Za-z\s]+$/)
  })

  test('logout returns to guest view with blur', async ({ page }) => {
    await page.locator('.btn-outline').click()
    await expect(page.locator('.header .btn-primary')).toHaveText('로그인')

    await page.locator('.post-card').first().click()
    await expect(page.locator('.post-body-section .blur-overlay')).toBeVisible({ timeout: 10000 })
    await expect(page.locator('.comment-section .blur-overlay')).toBeVisible({ timeout: 10000 })
  })
})
