import { Component, OnInit, ViewChild, ElementRef } from '@angular/core';

import { PresentationService } from '../services/presentation.service';
import { Presentation } from '../models/presentation.model';
import { Router, ActivatedRoute } from '@angular/router';
import { FormControl, Validators, FormBuilder, FormGroup } from '@angular/forms';
import { first } from 'rxjs/operators';
import { Message } from '../models/message.model';
import { Mark } from '../models/mark.model';
import { PresentationDTO } from '../models/dtos/presentation.dto';

@Component({
  selector: 'app-presentation-page',
  templateUrl: './presentation-page.component.html',
  styleUrls: ['./presentation-page.component.scss']
})
export class PresentationPageComponent implements OnInit {

  TYPE_FEEDBACK = 'TYPE_FEEDBACK';
  TYPE_QUESTION = 'TYPE_QUESTION';

  presentation: Presentation;
  currentRate = 0;
  submittedRate = false;
  submittedFeedback = false;
  feedbackAdded = false;
  canVote = false;
  myMark = 0;
  myEmail: string;
  avgMark = 0;
  ratingsCount = 0;
  editingMessage = -1;

  type: FormControl = this.fb.control(this.TYPE_FEEDBACK);
  anonymity: FormControl = this.fb.control(false);
  feedbackBox: FormControl = this.fb.control('', Validators.minLength(1));
  @ViewChild('fbb') feedbackBoxView: ElementRef;

  feedbackFormGroup: FormGroup = this.fb.group({
    feedbackBox: this.feedbackBox,
    type: this.type,
    anonymity: this.anonymity
  });

  constructor(
    private ps: PresentationService,
    private route: ActivatedRoute,
    private fb: FormBuilder
  ) { }

  ngOnInit() {

    this.myEmail = localStorage.getItem('email');

    this.route.data.subscribe((data: { presentation: Presentation }) => {
      this.presentation = data.presentation;

      this.avgMark = +this.ps.getAvgMark(this.presentation);

      this.ratingsCount = this.presentation.marks.length;

      const markObject = this.presentation.marks
        .find(mark => mark.email === this.myEmail);

      if (!markObject) {
        this.canVote = true;
      } else {
        this.myMark = markObject.mark;
        this.currentRate = markObject.mark;
      }
    });
  }

  submitRate(rate: number) {

    this.submittedRate = true;
    this.canVote = false;
    this.myMark = rate;
    this.currentRate = rate;

    if (!this.presentation.marks) {
      this.presentation.marks = [];
    }

    this.presentation.marks.push(new Mark({
      userId: +localStorage.getItem('userId'),
      email: localStorage.getItem('email'),
      mark: +rate.toFixed(2)
    }));

    this.avgMark = +this.ps.getAvgMark(this.presentation);

    this.ps.updatePresentation(this.presentation).pipe(first()).subscribe(
      data => {
        // alert('Succes!');
        // console.log('Succes!: ' + JSON.stringify(data));
      },
      error => {
        alert('Error!');
        console.log('Error!: ' + JSON.stringify(error));
      }
    );

  }

  editMessage(i: number) {
    // alert('editing message ' + i);

    this.editingMessage = i;

    this.feedbackBox.setValue(this.presentation.feedback[i].message);
    this.anonymity.setValue(this.presentation.feedback[i].anonymity);
    this.type.setValue(this.presentation.feedback[i].type);

    this.feedbackBoxView.nativeElement.focus();
  }

  deleteMessage(i: number) {
    alert(`Deleting message[${i}].id = ${this.presentation.feedback[i].id}`);
    this.ps.deleteMessage(this.presentation.feedback[i].id);
  }

  leaveFeedback(type: string) {

    this.submittedFeedback = true;

    const currentFeedback = new Message({
      email: (this.anonymity.value ? 'anonymous' : localStorage.getItem('email')),
      message: this.feedbackBox.value,
      type: this.type.value,
      anonymity: this.anonymity.value
    });

    if (this.editingMessage >= 0) {
      this.presentation.feedback[this.editingMessage] = currentFeedback;
      this.editingMessage = -1;
    } else {
      if (!this.presentation.feedback) {
        this.presentation.feedback = [];
      }
      this.presentation.feedback.push(currentFeedback);
    }

    this.ps.updatePresentation(this.presentation).pipe(first())
      .subscribe(
        data => {
          // alert('Succes!:' + JSON.stringify(data));
        },
        error => {
          alert('Error!: ' + error);
        }
      );

    this.feedbackFormGroup.reset({
      feedbackBox: '',
      type: 'feedback',
      anonymity: false
    });
  }

}
