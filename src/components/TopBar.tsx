import React, { type JSX } from 'react'

export default function TopBar(): JSX.Element {
  return (
    <header className="bg-black text-white py-4 px-6 flex justify-between items-center">
      <div className="text-xl font-bold">게젤샤프트</div>
      <nav className="space-x-6 text-center">
        <span>인격</span>
        <span>에고</span>
        <span>덱메이커</span>
        <span>거울던전</span>
      </nav>
    </header>
  )
}