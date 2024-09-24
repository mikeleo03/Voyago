import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environment/environment';
import { Payment } from '../../models/payment1.model';
import { HttpErrorResponse } from '@angular/common/http';
import { catchError, throwError } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class PaymentService {
  private apiUrl = `${environment.apiUrl}/payment`;

  constructor(private http: HttpClient) {}

  getPaymentById(paymentId: string): Observable<Payment> {
    return this.http.get<Payment>(`http://localhost:8080/api/v1/payment/${paymentId}`).pipe(
        catchError((error: HttpErrorResponse) => {
            console.error('Error fetching payment data:', error);
            return throwError(() => new Error('Failed to fetch payment data.'));
        })
    );
}


  getPaymentImage(id: string): Observable<Blob> {
    return this.http.get(`${this.apiUrl}/${id}/image`, { responseType: 'blob' });
  }

  changeVerifyStatus(id: string, status: string): Observable<Payment> {
    const params = new HttpParams().set('status', status);
    return this.http.put<Payment>(`${this.apiUrl}/verify/${id}`, null, { params });
  }
}
