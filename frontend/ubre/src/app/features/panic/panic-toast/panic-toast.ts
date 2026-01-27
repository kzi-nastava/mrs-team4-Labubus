import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-panic-toast',
  imports: [],
  templateUrl: './panic-toast.html',
  styleUrl: './panic-toast.css',
})
export class PanicToast {
  @Input() rideId: string = '';
  @Input() ALERT_ICON: string = 'warning_60dp_FFFFFF_FILL0_wght400_GRAD0_opsz48.svg';

  visible: boolean = false;

  ngOnInit() {}

  show(rideId: string) {
    this.rideId = rideId;
    this.visible = true;

    setTimeout(() => {
      this.visible = false;
    }, 5000);
  }
}
