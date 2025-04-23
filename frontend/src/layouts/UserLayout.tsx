import React from 'react';
import Header from '@/components/common/Header';
import Footer from '@/components/common/Footer';

const UserLayout: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  return (
    <div className="flex flex-col min-h-screen bg-[#F8FAFC]">
      <Header />
      <main className="container mx-auto px-4 py-8 flex-grow">{children}</main>
      <Footer />
    </div>
  );
};

export default UserLayout;