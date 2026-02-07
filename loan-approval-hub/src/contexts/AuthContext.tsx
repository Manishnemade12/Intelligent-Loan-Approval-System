import React, { createContext, useContext, useState, ReactNode } from 'react';
import { User, UserRole } from '@/types/loan';
import { mockUsers } from '@/data/mockData';

interface AuthContextType {
  user: User | null;
  login: (email: string, password: string) => Promise<boolean>;
  logout: () => void;
  switchRole: (role: UserRole) => void;
  isAuthenticated: boolean;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const AuthProvider = ({ children }: { children: ReactNode }) => {
  const [user, setUser] = useState<User | null>(mockUsers[1]); // Default to officer for demo

  const login = async (email: string, password: string): Promise<boolean> => {
    // Mock login - in production, this would call your API
    const foundUser = mockUsers.find(u => u.email === email);
    if (foundUser) {
      setUser(foundUser);
      return true;
    }
    return false;
  };

  const logout = () => {
    setUser(null);
  };

  const switchRole = (role: UserRole) => {
    const userForRole = mockUsers.find(u => u.role === role);
    if (userForRole) {
      setUser(userForRole);
    }
  };

  return (
    <AuthContext.Provider value={{
      user,
      login,
      logout,
      switchRole,
      isAuthenticated: !!user,
    }}>
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};
