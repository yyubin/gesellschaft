import React, { useEffect, useState, type JSX } from 'react'

export default function DdayCounter(): JSX.Element {
  const [timeLeft, setTimeLeft] = useState<string>('')

  useEffect(() => {
    const interval = setInterval(() => {
      const now = new Date()
      const nextThursday = new Date()
      nextThursday.setDate(
        now.getDate() + ((4 + 7 - now.getDay()) % 7 || 7) // 4 = Thursday
      )
      nextThursday.setHours(0, 0, 0, 0)

      const diff = nextThursday.getTime() - now.getTime()
      const hours = Math.floor((diff / (1000 * 60 * 60)) % 24)
      const minutes = Math.floor((diff / (1000 * 60)) % 60)
      const seconds = Math.floor((diff / 1000) % 60)
      const days = Math.floor(diff / (1000 * 60 * 60 * 24))

      setTimeLeft(`${days}일 ${hours}시간 ${minutes}분 ${seconds}초`)
    }, 1000)

    return () => clearInterval(interval)
  }, [])

  return (
    <div className="text-white text-2xl text-center py-4">
      거던 초기화까지 // <span className="font-bold">{timeLeft}</span>
    </div>
  )
}