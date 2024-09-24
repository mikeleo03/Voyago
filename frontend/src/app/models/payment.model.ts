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