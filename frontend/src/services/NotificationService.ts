import 'izitoast/dist/css/iziToast.min.css';
import iZtoast from 'izitoast';

class NotificationService {
  private readonly pos = 'topRight';

  showError(message: string, title = 'Error') {
    iZtoast.error({
      title: title,
      message: message,
      position: this.pos
    });
  }

  showSuccess(message: string, title = 'Success') {
    iZtoast.success({
      title: title,
      message: message,
      position: this.pos
    });
  }

  showInfo(message: string, title = 'Info') {
    iZtoast.info({
      title: title,
      message: message,
      position: this.pos
    });
  }
}

export default new NotificationService();
