import React, { type JSX } from 'react'

export default function TopBar(): JSX.Element {
  return (
    <header className="bg-zinc-900 text-white px-4 py-4 md:px-12 flex flex-col md:flex-row md:justify-between md:items-center gap-4 shadow-md">
      <div className="text-2xl font-extrabold text-white-500 tracking-wide text-center md:text-left">
        게젤샤프트
      </div>
      <nav className="flex flex-wrap justify-center gap-6 text-base md:text-lg font-medium text-white">
        <span>인격</span>
        <span>에고</span>
        <span>덱메이커</span>
        <span>거울던전</span>
      </nav>
    </header>
  )
}