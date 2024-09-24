// Main Model
export interface Payment {
    id: string;
    nominal: number;
    picture: string;
    paymentDate: Date;
    status: string;
    createdBy: string;
    createdAt: Date;
    updatedBy: string;
    updatedAt: Date;
}

export interface PaymentCreateDTO {
    nominal: number;
}
  
export interface PaymentUpdateDTO {
    status: string;
    picture?: string;
    paymentDate?: Date;
}