import React, { type JSX } from 'react'
import { Routes, Route } from 'react-router-dom'
import TopBar from './components/TopBar'
import Footer from './components/Footer'
import DdayCounter from './components/main/DdayCounter'
import EgoPage from './pages/EgoPage'
import PersonalityPage from './pages/PersonalityPage'

function App(): JSX.Element {
  return (
    <div className="min-h-screen flex flex-col bg-white text-black">
      <TopBar />
      <main className="flex-grow px-4 pt-10 pb-16 md:px-32">
        <Routes>
          <Route path="/" element={
            <section className="w-full px-4 md:px-16">
              <DdayCounter />
              <div className="mt-12 text-zinc-700 text-base md:text-lg leading-relaxed">
                이곳은 거울던전을 준비하는 공간입니다.<br />
                다양한 기능은 앞으로 이곳에 추가될 예정입니다.
              </div>
            </section>
          } />
          <Route path="/personas" element={<PersonalityPage />} />
          <Route path="/egos" element={<EgoPage />} />
        </Routes>

      </main>
      <Footer />
    </div>
  )
}

export default App
