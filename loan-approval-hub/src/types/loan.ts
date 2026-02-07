export type UserRole = 'customer' | 'officer' | 'admin';

export type LoanStatus = 'pending' | 'approved' | 'rejected' | 'manual_review';

export type LoanType = 'personal' | 'home' | 'auto' | 'business' | 'education';

export type EmploymentType = 'salaried' | 'self-employed' | 'business' | 'retired';

export interface LoanApplication {
  id: number;
  applicationId: string;
  applicantName: string;
  email: string;
  phone: string;
  loanType: LoanType;
  loanAmount: number;
  loanTerm: number;
  purpose: string;
  annualIncome: number;
  monthlyExpenses: number;
  creditScore: number;
  existingDebts: number;
  employmentType: EmploymentType;
  employmentDuration: number;
  employerName?: string;
  dtiRatio: number;
  ltiRatio: number;
  riskScore: number;
  status: LoanStatus;
  submittedAt: string;
  reviewedAt?: string;
  reviewedBy?: string;
  assignedOfficer?: string;
  aiExplanation?: string;
  aiSuggestions?: string;
  officerNotes?: string;
  documents: LoanDocument[];
  riskFactors?: RiskFactor[];
  createdAt?: string;
  updatedAt?: string;
}

export interface LoanDocument {
  id: string;
  name: string;
  type: 'salary_slip' | 'bank_statement' | 'id_proof' | 'address_proof' | 'other';
  uploadedAt: string;
  verified: boolean;
}

export interface RiskFactor {
  name: string;
  value: number;
  weight: number;
  score: number;
  status: string;
  extractedData?: Record<string, any>;
}

export interface RiskFactor {
  name: string;
  value: number;
  weight: number;
  score: number;
  status: 'good' | 'warning' | 'critical';
  description: string;
}

export interface DashboardStats {
  totalApplications: number;
  pending: number;
  approved: number;
  rejected: number;
  manualReview: number;
  avgProcessingTime: string;
  approvalRate: number;
}

export interface User {
  id: string;
  name: string;
  email: string;
  role: UserRole;
  avatar?: string;
}
