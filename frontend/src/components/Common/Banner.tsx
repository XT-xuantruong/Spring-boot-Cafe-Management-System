import React from 'react';
import { Link } from 'react-router-dom';
import Slider from 'react-slick';
import 'slick-carousel/slick/slick.css';
import 'slick-carousel/slick/slick-theme.css';
import { Button } from '@/components/ui/button';

const slides = [
  {
    title: 'Welcome to MyApp',
    description: 'Discover amazing features and join our community today!',
    cta: 'Get Started',
    link: '/register',
    background: 'from-blue-500 to-indigo-600',
  },
  {
    title: 'Explore Our Services',
    description: 'Experience top-notch services tailored just for you.',
    cta: 'Learn More',
    link: '/services',
    background: 'from-green-500 to-teal-600',
  },
  {
    title: 'Join Now',
    description: 'Be part of our growing community and unlock exclusive benefits.',
    cta: 'Sign Up',
    link: '/register',
    background: 'from-purple-500 to-pink-600',
  },
];

const Banner: React.FC = () => {
  const settings = {
    dots: true,
    infinite: true,
    speed: 500,
    slidesToShow: 1,
    slidesToScroll: 1,
    autoplay: true,
    autoplaySpeed: 3000,
    arrows: false,
  };

  return (
    <section className="relative h-96">
      <Slider {...settings}>
        {slides.map((slide, index) => (
          <div key={index} className="relative h-96">
            <div
              className={`absolute inset-0 bg-gradient-to-r ${slide.background} flex items-center justify-center`}
            >
              <div className="text-center text-white">
                <h1 className="text-4xl md:text-5xl font-bold mb-4">{slide.title}</h1>
                <p className="text-lg md:text-xl mb-6">{slide.description}</p>
                <Button asChild>
                  <Link
                    to={slide.link}
                    className="bg-white text-blue-600 px-6 py-3 rounded-full font-semibold hover:bg-gray-100 transition"
                  >
                    {slide.cta}
                  </Link>
                </Button>
              </div>
            </div>
          </div>
        ))}
      </Slider>
    </section>
  );
};

export default Banner;