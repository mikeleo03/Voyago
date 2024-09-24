import { Injectable } from '@angular/core';
import { environment } from '../../../environment/environment.prod';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Payment } from '../../models/payment.model';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class PaymentService {
  private apiUrl = `${environment.apiUrl}/payment`;

  constructor(private http: HttpClient) { }

  // Get payment by ID
  getPaymentById(id: string): Observable<Payment> {
    return this.http.get<Payment>(`${this.apiUrl}/${id}`);
  }

  // Upload the evidence
  uploadPaymentEvidence(id: string, imageFile?: File): Observable<Payment> {
    const formData: FormData = new FormData();

    if (imageFile) {
      formData.append('file', imageFile);
    }

    return this.http.put<Payment>(`${this.apiUrl}/payment/${id}`, formData);
  }

  getPaymentImage(id: string): Observable<Blob> {
    return this.http.get(`${this.apiUrl}/${id}/image`, { responseType: 'blob' });
  }

  changeVerifyStatus(id: string, status: string): Observable<Payment> {
    const params = new HttpParams().set('status', status);
    return this.http.put<Payment>(`${this.apiUrl}/verify/${id}`, null, { params });
  }
}
