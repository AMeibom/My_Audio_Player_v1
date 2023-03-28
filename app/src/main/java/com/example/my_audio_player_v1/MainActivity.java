package com.example.my_audio_player_v1;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private ImageView imagePlayPause;// Наша кнопка PLAY / PAUSE
    private TextView textCurrentTime; // Время начала трека
    private TextView textTotalDuration;// Общее время трека
    private SeekBar playerSeekBar;// создание поля SeekBar
    private MediaPlayer mediaPlayer;// Наш плеер
    private Handler handler= new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
      //  Привязываем  ссылки
        imagePlayPause = findViewById(R.id.imagePlayPause);
        textCurrentTime = findViewById(R.id.textCurrentTime);
        textTotalDuration= findViewById(R.id.textTotalDuration);
        playerSeekBar = findViewById(R.id.playerSeekBar);
        mediaPlayer = new MediaPlayer();

        playerSeekBar.setMax(100);

        imagePlayPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {   //Объявляем метод onClick, который будет вызываться при нажатии на кнопку.
                if (mediaPlayer.isPlaying()){ //Проверяем, играет ли в данный момент медиафайл.
                   handler.removeCallbacks(updater);//останавливаем обновление SeekBar
                   mediaPlayer.pause();// паузим воспроизведение медиафайл
                   imagePlayPause.setImageResource(R.drawable.play);//меняем изображение кнопки на "Play".
                }else{
                    mediaPlayer.start();//Если медиафайл не играет, то запускаем его воспроизведение
                    imagePlayPause.setImageResource(R.drawable.pause);//меняем изображение кнопки на "Pause"
                   updateSeekBar();//запускаем обновление SeekBar.
                }
            }
        });

        prepareMediaPlayer();//Вызываем метод prepareMediaPlayer(), который будет готовить медиаплеер к воспроизведению выбранного аудиофайла.

       // Устанавливаем обработчик касаний для SeekBar, чтобы пользователь мог перемещаться по аудиофайлу.
     playerSeekBar.setOnTouchListener(new View.OnTouchListener() {
             @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                SeekBar seekBar = (SeekBar)view;
                int playPosition = (mediaPlayer.getDuration()/100)*seekBar.getProgress();
                mediaPlayer.seekTo(playPosition);
                textCurrentTime.setText(milliSecondsToTimer(mediaPlayer.getCurrentPosition()));
                return false;
            }
        });

        // Устанавливаем обработчик для отслеживания процесса буферизации медиаплеера, чтобы установить соответствующую второстепенную прогрессию на SeekBar.
        mediaPlayer.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
            @Override
            public void onBufferingUpdate(MediaPlayer mp, int percent) {
                playerSeekBar.setSecondaryProgress(percent);
            }
        });

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() { //Устанавливаем слушатель завершения проигрывания для объекта mediaPlayer.
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {//Создаем анонимный класс, реализующий интерфейс OnCompletionListener.
                playerSeekBar.setProgress(0); //Переопределяем метод onCompletion для выполнения действий после завершения проигрывания.
                imagePlayPause.setImageResource(R.drawable.play);//Устанавливаем значок play на кнопке воспроизведения.
                textCurrentTime.setText(R.string.zero);//Устанавливаем текущее временя ь на начальное значение.
                textTotalDuration.setText(R.string.zero);// Устанавливаем общею длительность на начальное значение
                mediaPlayer.reset(); //Переопределяем метод onCompletion для выполнения действий после завершения проигрывания.
                prepareMediaPlayer();//Подготавливаем объект mediaPlayer для нового проигрывания файла.
            }
        }); //1


    }
       private void prepareMediaPlayer(){
        try {
            // указание источника аудио

            mediaPlayer = MediaPlayer.create(this, R.raw.komarovo);
            // mediaPlayer.setDataSource("https://ru.hitmotop.com/get/cuts/da/f5/daf56962e9b6032710873951725a0baa/48203943/BiS_-_Korabliki_b128f0d208.mp3");
            //mediaPlayer.prepare();// ассинхронная подготовка плеера к проигрыванию
            textTotalDuration.setText(milliSecondsToTimer(mediaPlayer.getDuration()));//устанавливает общую продолжительность аудио в текстовый элемент
        }catch (Exception exception){
            Toast.makeText(this,exception.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
    private Runnable updater = new Runnable() {// это объект Runnable, который будет использоваться для обновления SeekBar
        @Override                              //и текущего времени в текстовом элементе воспроизведения аудио.
        public void run() {
            updateSeekBar();//обновляет SeekBar и текущее время аудио в текстовом элементе, если аудио воспроизводится
            long currentDuration = mediaPlayer.getCurrentPosition();
            textCurrentTime.setText(milliSecondsToTimer(currentDuration));
        }
    };

    private void  updateSeekBar(){
        if(mediaPlayer.isPlaying()){
            playerSeekBar.setProgress((int) (((float) mediaPlayer.getCurrentPosition() / mediaPlayer.getDuration()) * 100));
            handler.postDelayed(updater, 1000);
        }
    }
    // преобразует миллисекунды продолжительности аудио в строку в формате "часы:минуты:секунды"
    private String milliSecondsToTimer (long milliSeconds){
        String timerString = "";
        String secondsString;
        int hours = (int) (milliSeconds/ (1000*60*60));
        int minutes = (int) (milliSeconds % (1000*60*60)/(1000*60));
        int seconds = (int) ((milliSeconds % (1000*60*60)) %(1000*60)/ 1000);

        if (hours>0) {
            timerString = hours + ":";
        }
        if (seconds<10){
            secondsString = "0" + seconds;
            }else {
            secondsString = "" + seconds;
        }
        timerString = timerString + minutes + ":" + secondsString;
        return timerString;
    }
}