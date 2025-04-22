import React from 'react';
import { Link } from 'react-router-dom';
import { Button } from '@/components/ui/button';

const slides = [
  {
    title: 'Savor the Moment',
    description: 'Indulge in our handcrafted coffee, brewed with love and care.',
    cta: 'Explore Menu',
    link: '/menu',
    background: 'from-[#FFFFFF] to-[#BFDBFE]',
    image: '/coffee-slide-1.jpg',
  },
  {
    title: 'Cozy Vibes Await',
    description: 'Join us for a warm and inviting coffee experience.',
    cta: 'Book a Table',
    link: '/reservations',
    background: 'from-[#FFFFFF] to-[#BFDBFE]',
    image: '/coffee-slide-2.jpg',
  },
  {
    title: 'Exclusive Offers',
    description: 'Sign up now to enjoy special discounts and loyalty rewards.',
    cta: 'Join Now',
    link: '/register',
    background: 'from-[#FFFFFF] to-[#BFDBFE]',
    image: '/coffee-slide-3.jpg',
  },
];

const Banner: React.FC = () => {
  // Chọn slide đầu tiên làm nội dung mặc định
  const defaultSlide = slides[0];

  return (
    <section className="relative h-96 mt-28">
      <div
        className={`absolute inset-0 bg-gradient-to-r ${defaultSlide.background} flex items-center justify-center`}
        style={{
          backgroundImage: `url(${defaultSlide.image})`,
          backgroundSize: 'cover',
          backgroundPosition: 'center',
        }}
      >
        <div className="text-center text-[#1E3A8A] bg-white bg-opacity-70 p-6 rounded-lg">
          <h1 className="text-4xl md:text-5xl font-bold mb-4">{defaultSlide.title}</h1>
          <p className="text-lg md:text-xl mb-6">{defaultSlide.description}</p>
          <Button asChild>
            <Link
              to={defaultSlide.link}
              className="bg-[#1E3A8A] text-white px-6 py-3 rounded-full font-semibold hover:bg-[#60A5FA] hover:text-[#1E3A8A] transition"
            >
              {defaultSlide.cta}
            </Link>
          </Button>
        </div>
      </div>
    </section>
  );
};

export default Banner;