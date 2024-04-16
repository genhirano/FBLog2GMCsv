# FBLog2GMCsv
Facebook Checkin log - TO -  GoogmeMap(MyMap) Plot import CSV
![overview](https://github.com/genhirano/FBLog2GMCsv/assets/3538386/d5cdedfa-6aef-468c-8073-1e875c3871f3)


## 概要
このプログラムは、Facebookから取得した投稿データのバックアップファイルから、そのチェックイン座標ををGoogleMap（マイマップ）にプロットするためのCSVファイルを生成します。

## 前提条件
* Facebookから、Json形式でバックアップファイルがダウンロード済みであること
  * Facebook Help 「自分の情報のコピーをダウンロードする」
    * https://www.facebook.com/help/212802592074644/?helpref=uf_share
* java11以上の実行環境およびMavenの実行環境が整っていること 

## 手順
* Facebookバックアップファイル(ZIPファイル)から、Post（投稿）データが格納されているファイルを取り出して、任意のフォルダにコピーしておく。
  * (ZIP)your_facebook_activity\posts\your_posts__check_ins__photos_and_videos_1.json
* 当プロジェクトを任意の場所にクローンする
* 以下コマンドで実行可能Jarファイルを生成する(targetフォルダが自動生成されます)
  * mvn install
* 生成されたjarファイルを実行する
  * java -jar [jarFilename]  [FacebookDownloadFileName.json]
    * ex) java -jar fblog2gmcsv-1.0-SNAPSHOT-jar-with-dependencies.jar ./data/your_posts__check_ins__photos_and_videos_1.json

* カレントフォルダに「output.csv」ファイルが生成されます
* GoogleMyMapを新規に作成し、レイヤーを新しく追加します。新しく作成された「無題のレイヤー」には「インポート」リンクがありますので、これをクリックし、output.csvを選択してインポートします。