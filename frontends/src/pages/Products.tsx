
import {
  Card,
  CardContent,
  CardFooter,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Plus, Edit, Trash2 } from "lucide-react";
import { useState } from "react";
import { Badge } from "@/components/ui/badge";
import { 
  Dialog, 
  DialogContent,
  DialogHeader,
  DialogTitle,
} from "@/components/ui/dialog";
import { ProductForm, ProductFormValues } from "@/components/products/ProductForm";
import { toast } from "@/hooks/use-toast";

interface Product {
  id: number;
  name: string;
  sku: string;
  unit: string;
  category: string;
  quantity: number;
  price: number;
}

const Products = () => {
  const [searchTerm, setSearchTerm] = useState("");
  const [isAddProductOpen, setIsAddProductOpen] = useState(false);
  const [products, setProducts] = useState<Product[]>([
    { id: 1, name: "ايفون ١٥ برو", sku: "IP15PRO", unit: "قطعة", category: "جوالات", quantity: 15, price: 4999 },
    { id: 2, name: "سامسونج جالكسي اس ٢٣", sku: "SGS23", unit: "قطعة", category: "جوالات", quantity: 3, price: 3899 },
    { id: 3, name: "ماك بوك برو", sku: "MBP2023", unit: "قطعة", category: "حواسيب", quantity: 7, price: 7499 },
    { id: 4, name: "ايباد برو", sku: "IPADPRO", unit: "قطعة", category: "أجهزة لوحية", quantity: 12, price: 3299 },
    { id: 5, name: "سماعات ايربودز", sku: "AIRPODS", unit: "قطعة", category: "اكسسوارات", quantity: 5, price: 899 },
    { id: 6, name: "ساعة ابل", sku: "AWATCH", unit: "قطعة", category: "ساعات ذكية", quantity: 4, price: 1599 },
    { id: 7, name: "جوال هواوي", sku: "HUAWEI", unit: "قطعة", category: "جوالات", quantity: 2, price: 2499 },
  ]);

  // Filter products based on search term
  const filteredProducts = products.filter(
    (product) =>
      product.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
      product.sku.toLowerCase().includes(searchTerm.toLowerCase()) ||
      product.category.toLowerCase().includes(searchTerm.toLowerCase())
  );

  const handleAddProduct = (values: ProductFormValues) => {
    const newProduct: Product = {
      id: products.length ? Math.max(...products.map((p) => p.id)) + 1 : 1,
      ...values,
    };
    
    setProducts([...products, newProduct]);
    setIsAddProductOpen(false);
    toast({
      title: "تم إضافة المنتج بنجاح",
      description: `تمت إضافة ${values.name} إلى قائمة المنتجات.`,
    });
  };

  return (
    <div className="space-y-6">
      <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4">
        <div className="w-full sm:w-64">
          <Input
            placeholder="بحث..."
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            className="w-full"
          />
        </div>
        <Button onClick={() => setIsAddProductOpen(true)}>
          <Plus className="ml-2 h-4 w-4" />
          إضافة منتج
        </Button>
      </div>

      <Card>
        <CardHeader>
          <CardTitle>قائمة المنتجات</CardTitle>
        </CardHeader>
        <CardContent>
          <div className="overflow-x-auto">
            <table className="w-full text-right">
              <thead>
                <tr className="border-b">
                  <th className="pb-2 pr-4 text-sm font-medium">المنتج</th>
                  <th className="pb-2 pr-4 text-sm font-medium">رمز المنتج</th>
                  <th className="pb-2 pr-4 text-sm font-medium">الوحدة</th>
                  <th className="pb-2 pr-4 text-sm font-medium">الفئة</th>
                  <th className="pb-2 pr-4 text-sm font-medium">الكمية</th>
                  <th className="pb-2 pr-4 text-sm font-medium">السعر</th>
                  <th className="pb-2 pr-4 text-sm font-medium">الإجراءات</th>
                </tr>
              </thead>
              <tbody>
                {filteredProducts.map((product) => (
                  <tr key={product.id} className="border-b">
                    <td className="py-3 pr-4">{product.name}</td>
                    <td className="py-3 pr-4">{product.sku}</td>
                    <td className="py-3 pr-4">{product.unit}</td>
                    <td className="py-3 pr-4">
                      <Badge variant="outline">{product.category}</Badge>
                    </td>
                    <td className="py-3 pr-4">
                      <span className={`font-medium ${product.quantity <= 5 ? "text-red-500" : ""}`}>
                        {product.quantity}
                      </span>
                    </td>
                    <td className="py-3 pr-4">{product.price} ر.س</td>
                    <td className="py-3 pr-4">
                      <div className="flex space-x-2">
                        <Button variant="ghost" size="icon" className="ml-2">
                          <Edit className="h-4 w-4" />
                        </Button>
                        <Button variant="ghost" size="icon" className="text-red-500">
                          <Trash2 className="h-4 w-4" />
                        </Button>
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </CardContent>
        <CardFooter className="flex justify-between border-t p-4">
          <div className="text-sm text-muted-foreground">
            إجمالي المنتجات: {filteredProducts.length}
          </div>
        </CardFooter>
      </Card>

      <Dialog open={isAddProductOpen} onOpenChange={setIsAddProductOpen}>
        <DialogContent className="sm:max-w-[600px]">
          <DialogHeader>
            <DialogTitle>إضافة منتج جديد</DialogTitle>
          </DialogHeader>
          <ProductForm 
            onSubmit={handleAddProduct} 
            onCancel={() => setIsAddProductOpen(false)} 
          />
        </DialogContent>
      </Dialog>
    </div>
  );
};

export default Products;
