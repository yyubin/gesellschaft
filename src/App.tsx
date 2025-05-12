import React, { type JSX } from 'react'
import TopBar from './components/TopBar'
import Footer from './components/Footer'
import DdayCounter from './components/main/DdayCounter'

function App(): JSX.Element {
  return (
    <div className="bg-black min-h-screen text-white flex flex-col">
      <TopBar />
      <main className="flex-grow flex flex-col items-center justify-start pt-12">
        <DdayCounter />
        {/* 추후 메인 콘텐츠가 추가될 수 있는 자리 */}
      </main>
      <Footer />
    </div>
  )
}
export default App
