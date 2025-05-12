import React, { type JSX } from 'react'
import { Link } from 'react-router-dom'

export default function TopBar(): JSX.Element {
  return (
    <header className="bg-zinc-900 text-white px-4 py-4 md:px-12 flex flex-col md:flex-row md:justify-between md:items-center gap-4 shadow-md">
      <Link to="/" className="text-2xl font-extrabold text-white-500 tracking-wide text-center md:text-left">
        게젤샤프트
      </Link>
      <nav className="flex flex-wrap justify-center gap-6 text-base md:text-lg font-medium text-white">
        <Link to="/인격" className="hover:text-white-400 transition-colors">인격</Link>
        <Link to="/에고" className="hover:text-white-400 transition-colors">에고</Link>
        <span>덱메이커</span>
        <span>거울던전</span>
      </nav>
    </header>
  )
}