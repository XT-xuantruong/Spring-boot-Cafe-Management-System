import { ReactNode } from "react";
import { Toaster } from "@/components/ui/toaster";
import Header from "@/components/common/Header";
import Footer from "@/components/common/Footer";

export default function AuthLayout({ children }: { children: ReactNode }) {
  return (
    <div className="flex flex-col min-h-screen bg-[#F8FAFC]">
      <Toaster />
      <Header />
      <div className="flex items-center justify-center flex-grow my-20">
        {children}
      </div>
      <Footer />
    </div>
  );
}