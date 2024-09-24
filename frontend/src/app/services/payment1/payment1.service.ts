import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environment/environment';
import { Payment } from '../../models/payment1.model';

@Injectable({
  providedIn: 'root'
})
export class PaymentService {
  private apiUrl = `${environment.apiUrl}/payment`;

  constructor(private http: HttpClient) {}

  getPaymentById(id: string): Observable<Payment> {
    return this.http.get<Payment>(`${this.apiUrl}/${id}`);
  }

  getPaymentImage(id: string): Observable<Blob> {
    return this.http.get(`${this.apiUrl}/${id}/image`, { responseType: 'blob' });
  }

  changeVerifyStatus(id: string, status: string): Observable<Payment> {
    const params = new HttpParams().set('status', status);
    return this.http.put<Payment>(`${this.apiUrl}/verify/${id}`, null, { params });
  }
}
