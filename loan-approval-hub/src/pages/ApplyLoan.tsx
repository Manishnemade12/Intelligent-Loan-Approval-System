import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import DashboardLayout from '@/components/layout/DashboardLayout';
import DocumentUpload from '@/components/common/DocumentUpload';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Textarea } from '@/components/ui/textarea';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select';
import { Form, FormControl, FormDescription, FormField, FormItem, FormLabel, FormMessage } from '@/components/ui/form';
import { Progress } from '@/components/ui/progress';
import { CheckCircle, ArrowLeft, ArrowRight, FileText, User, DollarSign, Building } from 'lucide-react';
import { toast } from 'sonner';

const applicationSchema = z.object({
  // Personal Info
  fullName: z.string().min(2, 'Name must be at least 2 characters'),
  email: z.string().email('Please enter a valid email'),
  phone: z.string().min(10, 'Please enter a valid phone number'),
  
  // Loan Details
  loanType: z.enum(['personal', 'home', 'auto', 'business', 'education']),
  loanAmount: z.coerce.number().min(1000, 'Minimum loan amount is $1,000'),
  loanTerm: z.coerce.number().min(12, 'Minimum term is 12 months'),
  purpose: z.string().min(10, 'Please describe the loan purpose'),
  
  // Financial Info
  annualIncome: z.coerce.number().min(1, 'Please enter your annual income'),
  monthlyExpenses: z.coerce.number().min(0, 'Please enter your monthly expenses'),
  creditScore: z.coerce.number().min(300).max(850, 'Credit score must be between 300-850'),
  existingDebts: z.coerce.number().min(0, 'Please enter existing debts'),
  
  // Employment
  employmentType: z.enum(['salaried', 'self-employed', 'business', 'retired']),
  employmentDuration: z.coerce.number().min(0, 'Please enter employment duration'),
  employerName: z.string().optional(),
  import DashboardLayout from '@/components/layout/DashboardLayout';
  import DocumentUpload from '@/components/common/DocumentUpload';
  import React, { useState } from 'react';
  import { api } from '../lib/api';
  import { LoanApplication } from '../types/loan';
type ApplicationFormData = z.infer<typeof applicationSchema>;

    const [form, setForm] = useState<Partial<LoanApplication>>({});
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState<string | null>(null);
    const [success, setSuccess] = useState(false);
  const [uploadedFiles, setUploadedFiles] = useState<File[]>([]);
    const handleSubmit = async () => {
      setLoading(true);
      setError(null);
      try {
        await api.createApplication(form);
        setSuccess(true);
      } catch (err) {
        setError('Failed to submit application');
      } finally {
        setLoading(false);
      }
    };
  });

  const totalSteps = 4;
  const progress = (step / totalSteps) * 100;

  const stepTitles = [
    { title: 'Personal Information', icon: User },
    { title: 'Loan Details', icon: FileText },
    { title: 'Financial Information', icon: DollarSign },
    { title: 'Documents', icon: Building },
  ];

  const onSubmit = (data: ApplicationFormData) => {
    console.log('Form submitted:', data);
    console.log('Uploaded files:', uploadedFiles);
    toast.success('Application submitted successfully!', {
      description: 'We will review your application and get back to you soon.',
    });
    navigate('/applications');
  };

  const nextStep = async () => {
    let fieldsToValidate: (keyof ApplicationFormData)[] = [];
    
    switch (step) {
      case 1:
        fieldsToValidate = ['fullName', 'email', 'phone'];
        break;
      case 2:
        fieldsToValidate = ['loanType', 'loanAmount', 'loanTerm', 'purpose'];
        break;
      case 3:
        fieldsToValidate = ['annualIncome', 'monthlyExpenses', 'creditScore', 'existingDebts', 'employmentType', 'employmentDuration'];
        break;
    }

    const result = await form.trigger(fieldsToValidate);
    if (result) {
      setStep((prev) => Math.min(prev + 1, totalSteps));
    }
  };

  const prevStep = () => {
    setStep((prev) => Math.max(prev - 1, 1));
  };

  return (
    <DashboardLayout title="Apply for Loan">
      <div className="max-w-3xl mx-auto animate-fade-in">
        {/* Progress Bar */}
        <div className="mb-8">
          <div className="flex justify-between mb-2">
            {stepTitles.map((s, index) => {
              const Icon = s.icon;
              const isActive = index + 1 === step;
              const isCompleted = index + 1 < step;
              
              return (
                <div
                  key={index}
                  className={`flex items-center gap-2 text-sm ${
                    isActive
                      ? 'text-primary font-medium'
                      : isCompleted
                      ? 'text-status-approved'
                      : 'text-muted-foreground'
                  }`}
                >
                  <div
                    className={`w-8 h-8 rounded-full flex items-center justify-center ${
                      isActive
                        ? 'bg-primary text-primary-foreground'
                        : isCompleted
                        ? 'bg-status-approved text-white'
                        : 'bg-secondary'
                    }`}
                  >
                    {isCompleted ? <CheckCircle className="w-4 h-4" /> : <Icon className="w-4 h-4" />}
                  </div>
                  <span className="hidden md:block">{s.title}</span>
                </div>
              );
            })}
          </div>
          <Progress value={progress} className="h-2" />
        </div>

        <Form {...form}>
          <form onSubmit={form.handleSubmit(onSubmit)}>
            <Card>
              <CardHeader>
                <CardTitle>{stepTitles[step - 1].title}</CardTitle>
                <CardDescription>
                  Step {step} of {totalSteps}
                </CardDescription>
              </CardHeader>
              <CardContent className="space-y-6">
                {/* Step 1: Personal Information */}
                {step === 1 && (
                  <div className="space-y-4">
                    <FormField
                      control={form.control}
                      name="fullName"
                      render={({ field }) => (
                        <FormItem>
                          <FormLabel>Full Name</FormLabel>
                          <FormControl>
                            <Input placeholder="John Doe" {...field} />
                          </FormControl>
                          <FormMessage />
                        </FormItem>
                      )}
                    />
                    <FormField
                      control={form.control}
                      name="email"
                      render={({ field }) => (
                        <FormItem>
                          <FormLabel>Email Address</FormLabel>
                          <FormControl>
                            <Input type="email" placeholder="john@example.com" {...field} />
                          </FormControl>
                          <FormMessage />
                        </FormItem>
                      )}
                    />
                    <FormField
                      control={form.control}
                      name="phone"
                      render={({ field }) => (
                        <FormItem>
                          <FormLabel>Phone Number</FormLabel>
                          <FormControl>
                            <Input placeholder="+1 (555) 123-4567" {...field} />
                          </FormControl>
                          <FormMessage />
                        </FormItem>
                      )}
                    />
                  </div>
                )}

                {/* Step 2: Loan Details */}
                {step === 2 && (
                  <div className="space-y-4">
                    <FormField
                      control={form.control}
                      name="loanType"
                      render={({ field }) => (
                        <FormItem>
                          <FormLabel>Loan Type</FormLabel>
                          <Select onValueChange={field.onChange} defaultValue={field.value}>
                            <FormControl>
                              <SelectTrigger>
                                <SelectValue placeholder="Select loan type" />
                              </SelectTrigger>
                            </FormControl>
                            <SelectContent>
                              <SelectItem value="personal">Personal Loan</SelectItem>
                              <SelectItem value="home">Home Loan</SelectItem>
                              <SelectItem value="auto">Auto Loan</SelectItem>
                              <SelectItem value="business">Business Loan</SelectItem>
                              <SelectItem value="education">Education Loan</SelectItem>
                            </SelectContent>
                          </Select>
                          <FormMessage />
                        </FormItem>
                      )}
                    />
                    <div className="grid grid-cols-2 gap-4">
                      <FormField
                        control={form.control}
                        name="loanAmount"
                        render={({ field }) => (
                          <FormItem>
                            <FormLabel>Loan Amount ($)</FormLabel>
                            <FormControl>
                              <Input type="number" {...field} />
                            </FormControl>
                            <FormMessage />
                          </FormItem>
                        )}
                      />
                      <FormField
                        control={form.control}
                        name="loanTerm"
                        render={({ field }) => (
                          <FormItem>
                            <FormLabel>Term (months)</FormLabel>
                            <FormControl>
                              <Input type="number" {...field} />
                            </FormControl>
                            <FormMessage />
                          </FormItem>
                        )}
                      />
                    </div>
                    <FormField
                      control={form.control}
                      name="purpose"
                      render={({ field }) => (
                        <FormItem>
                          <FormLabel>Loan Purpose</FormLabel>
                          <FormControl>
                            <Textarea
                              placeholder="Describe the purpose of this loan..."
                              className="min-h-[100px]"
                              {...field}
                            />
                          </FormControl>
                          <FormMessage />
                        </FormItem>
                      )}
                    />
                  </div>
                )}

                {/* Step 3: Financial Information */}
                {step === 3 && (
                  <div className="space-y-4">
                    <div className="grid grid-cols-2 gap-4">
                      <FormField
                        control={form.control}
                        name="annualIncome"
                        render={({ field }) => (
                          <FormItem>
                            <FormLabel>Annual Income ($)</FormLabel>
                            <FormControl>
                              <Input type="number" {...field} />
                            </FormControl>
                            <FormMessage />
                          </FormItem>
                        )}
                      />
                      <FormField
                        control={form.control}
                        name="monthlyExpenses"
                        render={({ field }) => (
                          <FormItem>
                            <FormLabel>Monthly Expenses ($)</FormLabel>
                            <FormControl>
                              <Input type="number" {...field} />
                            </FormControl>
                            <FormMessage />
                          </FormItem>
                        )}
                      />
                    </div>
                    <div className="grid grid-cols-2 gap-4">
                      <FormField
                        control={form.control}
                        name="creditScore"
                        render={({ field }) => (
                          <FormItem>
                            <FormLabel>Credit Score</FormLabel>
                            <FormControl>
                              <Input type="number" min={300} max={850} {...field} />
                            </FormControl>
                            <FormDescription>Between 300-850</FormDescription>
                            <FormMessage />
                          </FormItem>
                        )}
                      />
                      <FormField
                        control={form.control}
                        name="existingDebts"
                        render={({ field }) => (
                          <FormItem>
                            <FormLabel>Existing Debts ($)</FormLabel>
                            <FormControl>
                              <Input type="number" {...field} />
                            </FormControl>
                            <FormMessage />
                          </FormItem>
                        )}
                      />
                    </div>
                    <FormField
                      control={form.control}
                      name="employmentType"
                      render={({ field }) => (
                        <FormItem>
                          <FormLabel>Employment Type</FormLabel>
                          <Select onValueChange={field.onChange} defaultValue={field.value}>
                            <FormControl>
                              <SelectTrigger>
                                <SelectValue placeholder="Select employment type" />
                              </SelectTrigger>
                            </FormControl>
                            <SelectContent>
                              <SelectItem value="salaried">Salaried</SelectItem>
                              <SelectItem value="self-employed">Self-Employed</SelectItem>
                              <SelectItem value="business">Business Owner</SelectItem>
                              <SelectItem value="retired">Retired</SelectItem>
                            </SelectContent>
                          </Select>
                          <FormMessage />
                        </FormItem>
                      )}
                    />
                    <div className="grid grid-cols-2 gap-4">
                      <FormField
                        control={form.control}
                        name="employmentDuration"
                        render={({ field }) => (
                          <FormItem>
                            <FormLabel>Years at Current Job</FormLabel>
                            <FormControl>
                              <Input type="number" {...field} />
                            </FormControl>
                            <FormMessage />
                          </FormItem>
                        )}
                      />
                      <FormField
                        control={form.control}
                        name="employerName"
                        render={({ field }) => (
                          <FormItem>
                            <FormLabel>Employer Name (Optional)</FormLabel>
                            <FormControl>
                              <Input {...field} />
                            </FormControl>
                            <FormMessage />
                          </FormItem>
                        )}
                      />
                    </div>
                  </div>
                )}

                {/* Step 4: Documents */}
                {step === 4 && (
                  <div className="space-y-6">
                    <div>
                      <h4 className="font-medium mb-2">Required Documents</h4>
                      <ul className="text-sm text-muted-foreground space-y-1 mb-4">
                        <li>• Government-issued ID (passport, driver's license)</li>
                        <li>• Proof of income (salary slips, tax returns)</li>
                        <li>• Bank statements (last 3 months)</li>
                        <li>• Proof of address (utility bill, lease agreement)</li>
                      </ul>
                    </div>
                    <DocumentUpload onFilesChange={setUploadedFiles} maxFiles={5} />
                  </div>
                )}

                {/* Navigation Buttons */}
                <div className="flex justify-between pt-4">
                  <Button type="button" variant="outline" onClick={prevStep} disabled={step === 1}>
                    <ArrowLeft className="w-4 h-4 mr-2" />
                    Previous
                  </Button>
                  {step < totalSteps ? (
                    <Button type="button" onClick={nextStep}>
                      Next
                      <ArrowRight className="w-4 h-4 ml-2" />
                    </Button>
                  ) : (
                    <Button type="submit">
                      <CheckCircle className="w-4 h-4 mr-2" />
                      Submit Application
                    </Button>
                  )}
                </div>
              </CardContent>
            </Card>
          </form>
        </Form>
      </div>
    </DashboardLayout>
  );
};

export default ApplyLoan;
