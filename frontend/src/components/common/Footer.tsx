import React from 'react';
import { Link } from 'react-router-dom';

const Footer: React.FC = () => {
  return (
    <footer className="bg-[#FFFFFF] text-[#1E3A8A] py-8 shadow-inner">
      <div className="container mx-auto px-4">
        <div className="grid grid-cols-1 md:grid-cols-3 gap-8">
          <div>
            <h3 className="text-lg font-semibold mb-4 text-[#1E3A8A]">Coffee Shop</h3>
            <p className="text-[#1E3A8A]">
              Coffee shop is your cozy coffee haven, offering premium handcrafted drinks and a warm atmosphere.
            </p>
          </div>
          <div>
            <h3 className="text-lg font-semibold mb-4 text-[#1E3A8A]">Quick Links</h3>
            <ul className="space-y-2">
              <li>
                <Link to="/" className="text-[#1E3A8A] hover:text-[#60A5FA] transition">
                  Home
                </Link>
              </li>
              <li>
                <Link to="/menu" className="text-[#1E3A8A] hover:text-[#60A5FA] transition">
                  Menu
                </Link>
              </li>
              <li>
                <Link to="/about" className="text-[#1E3A8A] hover:text-[#60A5FA] transition">
                  About
                </Link>
              </li>
              <li>
                <Link to="/contact" className="text-[#1E3A8A] hover:text-[#60A5FA] transition">
                  Contact
                </Link>
              </li>
            </ul>
          </div>
          <div>
            <h3 className="text-lg font-semibold mb-4 text-[#1E3A8A]">Contact</h3>
            <p className="text-[#1E3A8A]">Email: xuantruong2k4.dev@gmail.com</p>
            <p className="text-[#1E3A8A]">Phone: +84 123 456 789</p>
            <p className="text-[#1E3A8A]">Address: Cam Le, Da Nang City, Viet Nam</p>
          </div>
        </div>
        <div className="mt-8 text-center text-[#1E3A8A]">
          © {new Date().getFullYear()} Nguyen Xuan Truong. All rights reserved.
        </div>
      </div>
    </footer>
  );
};

export default Footer;