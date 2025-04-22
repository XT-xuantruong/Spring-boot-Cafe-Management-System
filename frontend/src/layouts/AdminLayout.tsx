import React from 'react';
import { AppSidebarCustom } from '@/components/app-sidebar-custom';

interface AdminLayoutProps {
  children: React.ReactNode;
}

export default function AdminLayout({ children }: AdminLayoutProps) {
  return (
    <div className="flex flex-col min-h-screen bg-[#F8FAFC] w-full">
      <div className="flex flex-1 pt-16">
        <AppSidebarCustom className="w-64 bg-[#FFFFFF] border-r border-[#60A5FA] shadow-md" />
        <main className="flex-1 p-6 overflow-y-auto w-full">
          {children}
        </main>
      </div>
    </div>
  );
}