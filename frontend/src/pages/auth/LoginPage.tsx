/* eslint-disable @typescript-eslint/no-explicit-any */
import { z } from "zod";
import { useState } from "react";
import ReusableForm, {
  FormField,
  FormItem,
  FormLabel,
  FormControl,
  FormMessage,
  Input,
} from "@/components/ReusableForm";
import { Eye, EyeOff } from "lucide-react";
import { useToast } from "@/hooks/use-toast";
import { Link, useNavigate } from "react-router-dom";
import { useAuth } from "@/contexts/AuthContext";
import { authService } from "@/services/AuthServices";
import { userService } from "@/services/UserServices";

const loginSchema = z.object({
  email: z.string().email("Invalid email address"),
  password: z.string().min(6, "Password must be at least 6 characters"),
});

export default function LoginPage() {
  const [loading, setLoading] = useState(false);
  const [showPassword, setShowPassword] = useState(false);
  const { setAuthInfo } = useAuth()
  const navigate = useNavigate();
  const { toast } = useToast();

  const togglePasswordVisibility = () => setShowPassword(!showPassword);

  const onSubmit = async (formData: z.infer<typeof loginSchema>) => {
    setLoading(true);
    try {
      const response = await authService.login(formData.email, formData.password)
      if (response.data.status === "success") {
        const { access_token, refresh_token } = response.data.data;
        setAuthInfo({
            token:{
                access_token,
                refresh_token,
            },
            user: undefined,
        });
        const user = await userService.getProfile()
        if (user.status === 200) {
          setAuthInfo({
            token: {
              access_token,
              refresh_token,
            },
            user: user.data.data,
          });
        }
        toast({
          title: "Login successful",
          description: "Welcome back!",
        });
        console.log("User data:", user.data.data);
        
        if (user.data.data.role === "ADMIN") {
          navigate("/admin");
        } else {
          navigate("/profile");
        }
      } else {
        toast({
          variant: "destructive",
          title: "Login failed",
          description: response.data.message || "An unknown error occurred",
        });
      }
    } catch (err:any) {
      const errorMessage = err?.data?.message || "An unknown error occurred";
      toast({
        variant: "destructive",
        title: "Login failed!",
        description: errorMessage,
      });
      console.error("Login error:", err);
    } finally {
      setLoading(false);
    }
  };
  return (
      <div className="w-full max-w-md p-8 bg-white border rounded-lg shadow-lg">
        <h2 className="text-2xl font-bold mb-6 text-center text-gray-800">Sign In</h2>
        <ReusableForm
          schema={loginSchema}
          onSubmit={onSubmit}
          submitText={loading ? "Logging in..." : "Login"}
          defaultValues={{ email: "", password: "" }}
          isLoading={loading}
        >
          {({ control }) => (
            <>
              {/* Email Field */}
              <FormField
                control={control}
                name="email"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>Email</FormLabel>
                    <FormControl>
                      <Input type="email" placeholder="Enter your email" disabled={loading} {...field} />
                    </FormControl>
                    <FormMessage />
                  </FormItem>
                )}
              />

              {/* Password Field */}
              <FormField
                control={control}
                name="password"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>Password</FormLabel>
                    <FormControl>
                      <div className="relative">
                        <Input
                          type={showPassword ? "text" : "password"}
                          placeholder="Enter your password"
                          disabled={loading}
                          {...field}
                        />
                        <button
                          type="button"
                          className="absolute inset-y-0 right-3 flex items-center"
                          onClick={togglePasswordVisibility}
                        >
                          {showPassword ? <EyeOff size={20} /> : <Eye size={20} />}
                        </button>
                      </div>
                    </FormControl>
                    <FormMessage />
                  </FormItem>
                )}
              />
            </>
          )}
        </ReusableForm>
 
        <p className="text-center text-gray-600 mt-4">
          Don't have an account? <Link to="/register" className="underline hover:text-blue-900">Sign up now</Link>
        </p>
      </div>
  );
}