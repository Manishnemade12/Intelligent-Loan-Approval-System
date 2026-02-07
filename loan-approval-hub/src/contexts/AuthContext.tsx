import React, { createContext, useContext, useState, ReactNode, useEffect } from 'react';
import { User, UserRole } from '@/types/loan';
import { api } from '../lib/api';

interface AuthContextType {
  user: User | null;
  login: (email: string, password: string) => Promise<boolean>;
  logout: () => void;
  switchRole?: (role: UserRole) => void;
  isAuthenticated: boolean;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const AuthProvider = ({ children }: { children: ReactNode }) => {
  const [user, setUser] = useState<User | null>(null);

  useEffect(() => {
    // Try to fetch current user if JWT exists
    const token = localStorage.getItem('jwt');
    if (token && !user) {
      api.getCurrentUser()
        .then(data => setUser(data.user))
        .catch(() => setUser(null));
    }
  }, []);

  const login = async (email: string, password: string): Promise<boolean> => {
    try {
      const data = await api.login(email, password);
      localStorage.setItem('jwt', data.token);
      setUser(data.user);
      return true;
    } catch (err) {
      setUser(null);
      return false;
    }
  };

  const logout = () => {
    localStorage.removeItem('jwt');
    setUser(null);
  };

  return (
    <AuthContext.Provider value={{
      user,
      login,
      logout,
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
