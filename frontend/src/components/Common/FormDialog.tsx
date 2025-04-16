import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogTrigger } from "@/components/ui/dialog";
import { Button } from "@/components/ui/button";
import { Form, FormControl, FormField, FormItem, FormLabel } from "@/components/ui/form";
import { Input } from "@/components/ui/input";
import { useForm } from "react-hook-form";

interface FormFieldConfig {
  name: string;
  label: string;
  type?: string;
}

interface FormDialogProps {
  title: string;
  fields: FormFieldConfig[];
  onSubmit: (data: any) => void;
  triggerText: string;
  defaultValues?: any;
}

export const FormDialog = ({ title, fields, onSubmit, triggerText, defaultValues }: FormDialogProps) => {
  const form = useForm({ defaultValues });

  const handleSubmit = (data: any) => {
    onSubmit(data);
    form.reset();
  };

  return (
    <Dialog>
      <DialogTrigger asChild>
        <Button>{triggerText}</Button>
      </DialogTrigger>
      <DialogContent>
        <DialogHeader>
          <DialogTitle>{title}</DialogTitle>
        </DialogHeader>
        <Form {...form}>
          <form onSubmit={form.handleSubmit(handleSubmit)} className="space-y-4">
            {fields.map((field) => (
              <FormField
                key={field.name}
                control={form.control}
                name={field.name}
                render={({ field: formField }) => (
                  <FormItem>
                    <FormLabel>{field.label}</FormLabel>
                    <FormControl>
                      <Input
                        type={field.type || "text"}
                        {...formField}
                      />
                    </FormControl>
                  </FormItem>
                )}
              />
            ))}
            <Button type="submit" className="w-full">
              Submit
            </Button>
          </form>
        </Form>
      </DialogContent>
    </Dialog>
  );
};