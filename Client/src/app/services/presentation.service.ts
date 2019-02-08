import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Presentation } from '../models/presentation.model';
import { environment } from 'src/environments/environment.prod';
import { PresentationDTO } from '../models/dtos/presentation.dto';

const SERVER_URL = environment.serverUrl;
const MSG_API = environment.messagesApiRoute;
const PRESENTATIONS_API = environment.presentationsApiRoute;

@Injectable({
  providedIn: 'root'
})
export class PresentationService {

  constructor(private http: HttpClient) { }

  createPresentation(presentation: Presentation) {
    return this.http.post<PresentationDTO>(SERVER_URL + PRESENTATIONS_API, Presentation.toDTO(presentation));
  }

  getPresentations() {
    return this.http.get<PresentationDTO[]>(SERVER_URL + PRESENTATIONS_API);
  }

  getPresentationsByUser(userId: string) {
    return this.http.get<PresentationDTO[]>(SERVER_URL + PRESENTATIONS_API, {
      params: {
        userId: '' + userId
      }
    });
  }

  getPresentationsByTitle(title: string) {
    return this.http.get<PresentationDTO[]>(SERVER_URL + PRESENTATIONS_API, {
      params: {
        title_like: title
      }
    });
  }

  getPresentationsByEmail(email: string) {
    return this.http.get<PresentationDTO[]>(SERVER_URL + PRESENTATIONS_API, {
      params: {
        email_like: email
      }
    });
  }

  getPresentationById(id: string) {
    return this.http.get<PresentationDTO>(SERVER_URL + PRESENTATIONS_API + `/${id}`);
  }

  updatePresentation(presentation: Presentation) {
    return this.http.put<PresentationDTO>(SERVER_URL + PRESENTATIONS_API + `/${presentation.id}`, Presentation.toDTO(presentation));
  }

  deletePresentation(id: string) {
    return this.http.delete<PresentationDTO>(SERVER_URL + PRESENTATIONS_API + `/${id}`);
  }

  deleteMessage(id: string) {
    return this.http.delete(SERVER_URL + MSG_API + `/${id}`);
  }

  getAvgMark(presentation: Presentation) {

    let value = Number(0).toFixed(2);

    if (presentation.marks) {
      let sum = 0;
      presentation.marks.forEach(element => sum += element.mark);
      value = Number(sum / presentation.marks.length).toFixed(2);
    }

    return value;
  }
}
