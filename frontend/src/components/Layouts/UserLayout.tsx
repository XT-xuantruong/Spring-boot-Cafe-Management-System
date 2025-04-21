import React from 'react';
import Header from '../Common/Header';
import Banner from '../Common/Banner';
import Footer from '../Common/Footer';

const UserLayout: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  return (
    <div className="flex flex-col min-h-screen bg-gray-100">
      <Header />
      <Banner />
      <main className="container mx-auto px-4 py-8 flex-grow">
        {children}
      </main>
      <Footer />
    </div>
  );
};

export default UserLayout;