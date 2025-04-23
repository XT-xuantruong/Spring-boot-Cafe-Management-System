import React from 'react';
import { useGetAllTablesQuery } from '@/services/cafeTableServices';
import { useGetAllMenuItemsQuery } from '@/services/menuItemServices';
import { CafeTable } from '@/interfaces/cafetable';
import { MenuItem } from '@/interfaces/menuItem';
import { Button } from '@/components/ui/button';
import { Card, CardContent, CardFooter, CardHeader, CardTitle } from '@/components/ui/card';
import { Loader2 } from 'lucide-react';
import { useNavigate } from 'react-router-dom';
import { TableStatus } from '@/enums/tableStatus';
import Banner from '@/components/common/Banner';

const getStatusBadgeClass = (status: TableStatus): string => {
  switch (status) {
    case TableStatus.AVAILABLE:
      return 'bg-green-100 text-green-800';
    case TableStatus.RESERVED:
      return 'bg-yellow-100 text-yellow-800';
    case TableStatus.OCCUPIED:
      return 'bg-red-100 text-red-800';
    default:
      return 'bg-gray-100 text-gray-800';
  }
};

const HomePage: React.FC = () => {
  const { data: tablesData, isLoading: isTablesLoading } = useGetAllTablesQuery();
  const { data: menuItemsData, isLoading: isMenuItemsLoading } = useGetAllMenuItemsQuery();
  const navigate = useNavigate();

  if (isTablesLoading || isMenuItemsLoading) {
    return (
      <div className="flex justify-center items-center h-screen">
        <Loader2 className="h-8 w-8 animate-spin" />
      </div>
    );
  }

  const tables = tablesData?.data || [];
  const menuItems = menuItemsData?.data || [];

  return (
    <div className="container mx-auto p-6">
      <Banner />
      {/* Tables Section */}
      <section className="mb-12">
        <h2 className="text-2xl font-semibold mb-4">Available Tables</h2>
        <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 gap-6">
          {tables.length === 0 ? (
            <p className="text-center col-span-full">No available tables at the moment.</p>
          ) : (
            tables.map((table: CafeTable) => (
              <Card key={table.tableId} className="hover:shadow-lg transition-shadow">
                <CardHeader>
                  <CardTitle>Table {table.tableNumber}</CardTitle>
                </CardHeader>
                <CardContent>
                  <p>
                    <strong>Status:</strong>{' '}
                    <span
                      className={`inline-block px-2 py-1 rounded-full text-xs font-semibold ${getStatusBadgeClass(
                        table.status
                      )}`}
                    >
                      {table.status}
                    </span>
                  </p>
                  <p><strong>Capacity:</strong> {table.capacity} people</p>
                </CardContent>
                <CardFooter>
                  {table.status === TableStatus.AVAILABLE && (
                    <Button
                      onClick={() => navigate(`/reservation?tableId=${table.tableId}`)}
                    >
                      Reserve Table
                    </Button>
                  )}
                </CardFooter>
              </Card>
            ))
          )}
        </div>
      </section>

      {/* Menu Items Section */}
      <section>
        <h2 className="text-2xl font-semibold mb-4">Menu</h2>
        <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 gap-6">
          {menuItems.map((item: MenuItem) => (
            <Card key={item.itemId} className="hover:shadow-lg transition-shadow pt-6">
              
              <CardContent>
                {item.imageUrl && (
                  <img
                    src={item.imageUrl}
                    alt={item.itemName}
                    className="w-full h-40 object-cover rounded-md mb-4"
                  />
                )}
                <CardTitle>{item.itemName}</CardTitle>
                <p className="text-gray-600">{item.description}</p>
                <p className="font-semibold mt-2">${item.price.toFixed(2)}</p>
              </CardContent>
              <CardFooter>
                <Button
                  onClick={() => navigate(`/order?menuItemId=${item.itemId}`)}
                >
                  Order Now
                </Button>
              </CardFooter>
            </Card>
          ))}
        </div>
      </section>
    </div>
  );
};

export default HomePage;