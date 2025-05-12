import React, { type JSX } from 'react'

export default function Footer(): JSX.Element {
  return (
    <footer className="bg-zinc-900 text-zinc-400 text-sm text-center py-6 border-t border-zinc-800">
      ⓒ 2025 <div className="text-white font-semibold">Gesellschaft</div>. All rights reserved.
    </footer>
  )
}