package com.eyeem.recyclerviewtools.sample;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Random;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by budius on 13.03.15.
 */
public class Adapter extends RecyclerView.Adapter<Adapter.Holder> {

   private static final Random RANDOM = new Random();

   private LayoutInflater inflater;
   private int thumbSize = -1;

   private final ArrayList<Data> data;
   boolean vertical = false;

   static class Data {
      String img;
      String text;

      Data(String text, String img) {
         this.img = img;
         this.text = text;
      }
   }

   public int addStart() {
      int val = RANDOM.nextInt(3) + 1;
      for (int i = 0; i < val; i++)
         data.add(0, new Data(LOREM_IPSUM[RANDOM.nextInt(LOREM_IPSUM.length)], IMAGES[RANDOM.nextInt(IMAGES.length)]));
      return val;
   }

   public int addEnd() {
      int val = RANDOM.nextInt(3) + 3;
      for (int i = 0; i < val; i++)
         data.add(new Data(LOREM_IPSUM[RANDOM.nextInt(LOREM_IPSUM.length)], IMAGES[RANDOM.nextInt(IMAGES.length)]));
      return val;
   }

   public void remove(int position) {
      data.remove(position);
   }

   public Adapter() {
      data = new ArrayList<>();
      for (int i = 0; i < RANDOM.nextInt(10) + 35; i++) {
         data.add(new Data(LOREM_IPSUM[i], IMAGES[i]));
      }
   }

   @Override public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
      if (inflater == null)
         inflater = LayoutInflater.from(parent.getContext());
      if (thumbSize == -1)
         thumbSize = parent.getResources().getDimensionPixelSize(R.dimen.thumb_size);
      LinearLayout v = (LinearLayout) inflater.inflate(R.layout.adapter, parent, false);
      if (vertical)
         v.setOrientation(LinearLayout.VERTICAL);
      return new Holder(v);
   }

   @Override public void onBindViewHolder(Holder holder, int position) {
      String url = data.get(position).img.replace("/100/", "/" + thumbSize + "/");
      Picasso.with(holder.img.getContext())
         .load(url)
         .resize(thumbSize, thumbSize)
         .centerCrop()
         .into(holder.img);
      holder.txt.setText(data.get(position).text);
   }

   @Override public int getItemCount() {
      return data.size();
   }

   /**
    * This class contains all butterknife-injected Views & Layouts from layout file 'adapter.xml'
    * for easy to all layout elements.
    *
    * @author ButterKnifeZelezny, plugin for Android Studio by Avast Developers (http://github.com/avast)
    */
   static class Holder extends RecyclerView.ViewHolder {
      @InjectView(R.id.img) ImageView img;
      @InjectView(R.id.txt) TextView txt;

      Holder(View itemView) {
         super(itemView);
         ButterKnife.inject(this, itemView);
      }
   }

   public static String getRandomImage() {
      return IMAGES[RANDOM.nextInt(IMAGES.length)];
   }

   // some images from the popular feed from EyeEm on the 13.03.2015
   private static final String[] IMAGES = {
      "http://www.eyeem.com/thumb/h/100/f978dff44c76190055f964aedd05dd196f00dcba-1426221883",
      "http://www.eyeem.com/thumb/h/100/f0dc7e3274bf3c1d71137a726a83ad7884d691cb-1426220990",
      "http://www.eyeem.com/thumb/h/100/eb58c8a9d85756a57d0249c381452516ea1d5bee-1426221910",
      "http://www.eyeem.com/thumb/h/100/eaebeac69797df3e05a2143fa2239969028b6f6c-1426222680",
      "http://www.eyeem.com/thumb/h/100/e8f92eb8a5df92d4f583b54c46753d6bdc2d4140-1426221363",
      "http://www.eyeem.com/thumb/h/100/e3b74d48d97c94ed1b0245378387f89441d71a74-1426212254",
      "http://www.eyeem.com/thumb/h/100/de5eb9492248443925e250663c7bdad8bba38633-1426217057",
      "http://www.eyeem.com/thumb/h/100/d59935cc46cb45d87360996d516e2d1dc03ad287-1426217867",
      "http://www.eyeem.com/thumb/h/100/d4ca440942ad841500f8d2f883abe23adfdeebea-1426219560",
      "http://www.eyeem.com/thumb/h/100/cf9b97401f54bb04cc7fad62451aa0b54e3d8688-1426221348",
      "http://www.eyeem.com/thumb/h/100/ce6fc8bf60181570f24d27735395cc6e2c544ada-1426218275",
      "http://www.eyeem.com/thumb/h/100/cd4d3bf9edfe11da3d42e8d79c70846817c30a3a-1426216120",
      "http://www.eyeem.com/thumb/h/100/c94db98fefccc758f648469fb05e937d7c721fe0-1426214938",
      "http://www.eyeem.com/thumb/h/100/c8f49a3a01d9ffaac1f5f0e02626db078d83826b-1426211287",
      "http://www.eyeem.com/thumb/h/100/c2334a2fb7f42c6cf7a9d6dece0f815ebd889be1-1426220639",
      "http://www.eyeem.com/thumb/h/100/ae36d3b2d1c72d6ef3f08ed0a7aa5c2a22724697-1426218571",
      "http://www.eyeem.com/thumb/h/100/acc6050c37b19c53fd024b49637c8a1eeefe1f48-1426220566",
      "http://www.eyeem.com/thumb/h/100/aa7e0d28dec4120f43aa91db34fcf885671be488-1426221342",
      "http://www.eyeem.com/thumb/h/100/9f9381aaa37ad42b8f137d9f2bd1499ea86aab59-1426220547",
      "http://www.eyeem.com/thumb/h/100/89014e4c5368f40be5d3fcd0add9a73f5469bad0-1426221714",
      "http://www.eyeem.com/thumb/h/100/87b58f743ca363aad996b0ba631e4c21c9d6c54e-1426220290",
      "http://www.eyeem.com/thumb/h/100/863ea3855f58e7345af1760fc7dd35f526412930-1426220834",
      "http://www.eyeem.com/thumb/h/100/85821346a53a6edc8d9cdacb3f3b96904decfc61-1426219977",
      "http://www.eyeem.com/thumb/h/100/7f6484eb5a5867e8a9502d224e30ba3e7c36b52e-1426221533",
      "http://www.eyeem.com/thumb/h/100/7eb8fa379a7c8cdf7673dd7a0a9671f78dbd6f80-1426220886",
      "http://www.eyeem.com/thumb/h/100/7d7df5f3247f3823daa365142414a9c8bafc0c95-1426221877",
      "http://www.eyeem.com/thumb/h/100/7388c9db2f18f4629390fd0cdbc4f2d1e8581338-1426221585",
      "http://www.eyeem.com/thumb/h/100/722392a8e942767939e27b967e8cb24802c486d9-1426215966",
      "http://www.eyeem.com/thumb/h/100/70db64232168b6703615c550682e280ecd3dfb7d-1426220112",
      "http://www.eyeem.com/thumb/h/100/6bdf34696c240e748807557ac36596670cdc4d99-1426220339",
      "http://www.eyeem.com/thumb/h/100/67b0b4870edbc7974f4fa0b7d0b28b36eaf36d46-1426205503",
      "http://www.eyeem.com/thumb/h/100/668d3e5c081ee460d3f67d4a237716db88385a24-1426217583",
      "http://www.eyeem.com/thumb/h/100/5d227036c0556259535d2590dd9ebfaa86835dda-1426216818",
      "http://www.eyeem.com/thumb/h/100/5637b468d431d7357f2ef7713d2353a62aef926c-1426220233",
      "http://www.eyeem.com/thumb/h/100/543aa8725eb65393351bd186da4e46eb52ae8b2f-1426219610",
      "http://www.eyeem.com/thumb/h/100/50f802c312ae119c52a5449ca2e4dee18935e13c-1426222731",
      "http://www.eyeem.com/thumb/h/100/4bba49fe461496c795c7aaeed1302bfc46c97963-1426220833",
      "http://www.eyeem.com/thumb/h/100/4b04d7db2673dea9c122103f4812806023fd99bf-1426219122",
      "http://www.eyeem.com/thumb/h/100/494b7581b95a6b41a586cfc558251284836cccfc-1426219578",
      "http://www.eyeem.com/thumb/h/100/3529de2886b38507cc1f16bd72baaab29e0c6246-1426222265",
      "http://www.eyeem.com/thumb/h/100/32e9b00e6d46f7157b07706e1f0a2ea01507f257-1426210938",
      "http://www.eyeem.com/thumb/h/100/3143df51426a207b326ce9371045952268727752-1426222675",
      "http://www.eyeem.com/thumb/h/100/304ce8142fdbeae731c7dca33ff231faf4237b54-1426220803",
      "http://www.eyeem.com/thumb/h/100/22e39bff1657b760ec3ac58e1c6a70890d2a9efe-1426221057",
      "http://www.eyeem.com/thumb/h/100/1894c25b50e6c72dab6e1c498e4eee4df6a27fe7-1426218706",
      "http://www.eyeem.com/thumb/h/100/170eaf0a73c377361ce21e093e17ac102973aa0d-1426222442",
      "http://www.eyeem.com/thumb/h/100/14d974f7fdb5d2ad946bef4e0aac6f872fe846c6-1426221150",
      "http://www.eyeem.com/thumb/h/100/0a27bb06da01a1c9e79b474bfd4fcbe11980c7df-1426220721",
      "http://www.eyeem.com/thumb/h/100/088705c6e4b2b78a75ac131844883909ce5d02c5-1426222771",
      "http://www.eyeem.com/thumb/h/100/020df6440692a0dcc2aece0bf829761dace23455-1426216257"
   };

   // from: http://www.lipsum.com/feed/html
   // Generated 50 paragraphs, 1337 words, 9973 bytes of Lorem Ipsum
   private static final String[] LOREM_IPSUM = {
      "Lorem ipsum dolor sit amet, consectetur adipiscing elit.",
      "Integer aliquet ipsum eget est lacinia, a commodo risus accumsan.",
      "Fusce scelerisque quam sed purus imperdiet convallis.",
      "Maecenas dignissim lacus a justo suscipit, ut consequat leo accumsan.",
      "Vestibulum quis nulla quis quam lacinia convallis eget non nunc.",
      "Proin ultrices massa nec tincidunt tempus.",
      "Mauris scelerisque quam sit amet rhoncus tempus.",
      "Proin vel nunc vitae neque lacinia facilisis semper non urna.",
      "Donec condimentum eros ut orci tincidunt, at consectetur urna rhoncus.",
      "Etiam blandit orci blandit ipsum condimentum facilisis.",
      "Curabitur quis nibh at nibh rhoncus gravida et non felis.",
      "Ut consectetur turpis quis vehicula rhoncus.",
      "Sed at eros in sem consectetur malesuada.",
      "Sed vitae nunc fermentum, tincidunt odio sit amet, egestas libero.",
      "Praesent non leo nec neque tempus porta.",
      "Donec fringilla orci vitae eleifend sagittis.",
      "Integer quis felis eu magna auctor porttitor ac a felis.",
      "Donec id magna rhoncus, convallis augue a, rutrum purus.",
      "Proin viverra sem eget lorem laoreet dictum.",
      "Morbi maximus risus at tortor vestibulum ullamcorper.",
      "Vestibulum sagittis justo nec rhoncus vulputate.",
      "Suspendisse eu massa eget libero pulvinar congue.",
      "Duis condimentum ligula at odio mattis rutrum.",
      "Suspendisse venenatis sapien id tristique auctor.",
      "Nulla vulputate nulla ac neque hendrerit, at viverra felis maximus.",
      "Ut et lectus vehicula, fermentum libero sodales, luctus risus.",
      "Proin pharetra arcu vitae commodo feugiat.",
      "In et urna pulvinar, condimentum enim a, blandit metus.",
      "Quisque consectetur massa eu dignissim gravida.",
      "Suspendisse auctor diam aliquet feugiat sagittis.",
      "Praesent placerat turpis pulvinar enim sagittis, eget dapibus diam placerat.",
      "Sed aliquet libero ac risus pellentesque, viverra euismod lacus interdum.",
      "Fusce vitae purus ut velit volutpat luctus eu in nisl.",
      "Donec porttitor ex id dapibus congue.",
      "Maecenas ultrices enim ac faucibus ullamcorper.",
      "Sed varius dolor id ligula auctor dignissim.",
      "Aenean aliquam leo vel mi eleifend, et porta elit rhoncus.",
      "Proin scelerisque orci sed nibh imperdiet tempor.",
      "Suspendisse consectetur tortor nec rhoncus rhoncus.",
      "Maecenas viverra ex id odio dignissim, quis tristique velit pharetra.",
      "Vestibulum facilisis ante at quam finibus, non volutpat mi scelerisque.",
      "Nunc laoreet velit at arcu molestie, vel volutpat metus pharetra.",
      "Etiam sed turpis a odio volutpat mattis sagittis non lectus.",
      "Curabitur consectetur purus eu nulla tristique, vitae dictum enim porta.",
      "Fusce tincidunt justo non risus vehicula, ut hendrerit tortor gravida.",
      "Sed non augue sed velit posuere facilisis.",
      "Nullam iaculis dui ut nulla ornare finibus.",
      "Quisque quis lacus a magna facilisis tristique vel convallis massa.",
      "Duis congue lectus vel justo fermentum, nec aliquet erat lacinia.",
      "Integer nec leo tempus, interdum metus vel, pellentesque lorem.",
      "Etiam vitae elit ac orci ultrices ullamcorper.",
      "Nunc ut ante interdum, laoreet mauris sed, gravida ligula.",
      "Cras sollicitudin nunc sed bibendum lacinia.",
      "Aliquam vel mauris venenatis, mollis sapien ut, porta justo.",
      "Etiam pharetra metus et enim iaculis, id venenatis nisi sagittis.",
      "Nunc aliquam tortor elementum ultricies tincidunt.",
      "Proin dictum velit quis justo fermentum laoreet.",
      "Cras ultrices justo sed sapien rutrum lobortis.",
      "In at urna eget tellus vulputate egestas.",
      "Nunc a tortor interdum, bibendum velit eu, mattis nunc.",
      "Etiam laoreet lacus in erat ultrices, eu tincidunt neque egestas.",
      "In eleifend velit a turpis sagittis interdum.",
      "Cras auctor urna sit amet tincidunt rutrum.",
      "Nam sit amet eros eu est faucibus tristique vitae rutrum magna.",
      "Duis eu libero vitae magna lobortis tempus.",
      "Pellentesque aliquam metus at urna posuere, et mollis eros pellentesque.",
      "Integer hendrerit ipsum id facilisis molestie.",
      "Curabitur finibus urna id dolor euismod ornare.",
      "Phasellus eu tortor maximus, auctor magna et, pulvinar leo.",
      "Nunc aliquam nisl vitae velit porttitor iaculis.",
      "Vestibulum sit amet urna in nisi rhoncus rutrum.",
      "Sed at erat sed magna dignissim faucibus non ut elit.",
      "Nullam bibendum nibh vel consequat feugiat.",
      "Pellentesque ultrices massa vel sollicitudin ornare.",
      "Vivamus ut nunc quis libero laoreet maximus.",
      "Mauris tempor diam non elit efficitur condimentum.",
      "Aenean sit amet augue id dolor mattis tempor.",
      "Curabitur accumsan odio quis ligula luctus, quis efficitur magna elementum.",
      "Cras mattis erat maximus, molestie nunc a, commodo turpis.",
      "Quisque in purus non neque sollicitudin iaculis.",
      "Morbi consequat tellus sit amet nibh auctor iaculis.",
      "Donec maximus augue at pulvinar congue.",
      "Etiam placerat orci quis ultrices eleifend.",
      "Cras pretium odio vel vulputate semper.",
      "Ut vitae odio vel leo tempus ullamcorper.",
      "Vestibulum aliquet velit at lacus facilisis, vel vestibulum libero euismod.",
      "Mauris eget nibh sit amet magna tempor commodo.",
      "Nunc nec elit at risus condimentum ullamcorper.",
      "Vestibulum a magna et urna lacinia porta.",
      "Pellentesque suscipit orci dapibus lorem fringilla commodo.",
      "Ut nec ex sit amet sem pulvinar dignissim et id ex.",
      "Fusce lacinia lorem quis imperdiet iaculis.",
      "Etiam id turpis fermentum, suscipit magna id, facilisis dolor.",
      "Donec aliquet tortor sit amet lacinia convallis.",
      "Donec non sem suscipit, vehicula leo quis, vehicula diam.",
      "Cras non lacus eu leo imperdiet dapibus.",
      "Sed elementum risus non efficitur congue.",
      "Integer quis massa id lorem consectetur auctor quis a sapien.",
      "Aenean eu dolor ultricies, feugiat velit sit amet, vehicula augue.",
      "Proin rhoncus ante quis tincidunt luctus.",
      "Nunc malesuada nisl sed massa venenatis volutpat.",
      "Maecenas at felis a augue pulvinar gravida eu a lectus.",
      "Vestibulum et nunc eu augue imperdiet pretium.",
      "Suspendisse tristique sapien at lorem rhoncus facilisis.",
      "Nam malesuada mauris sed ante tincidunt, vel sagittis lectus tincidunt.",
      "Ut et nisl quis tellus efficitur porttitor sed a ante.",
      "Suspendisse at augue et eros scelerisque tincidunt.",
      "Suspendisse vitae ex id lorem mollis porttitor.",
      "Aliquam gravida nisl non volutpat suscipit.",
      "Donec laoreet ipsum euismod dui commodo lobortis eu quis dolor.",
      "Nulla tempus dui a augue lobortis, non sollicitudin eros dapibus.",
      "Aliquam ac lacus eu quam posuere rutrum.",
      "Integer euismod turpis ac sem tempus feugiat.",
      "Aliquam et purus vitae dolor tempus congue volutpat nec velit.",
      "Nulla quis ipsum interdum, consectetur neque vel, tincidunt est.",
      "Integer ut purus aliquet mauris pharetra egestas.",
      "Nulla sit amet massa sodales, porttitor odio suscipit, pretium magna.",
      "Duis ut massa feugiat, semper risus at, convallis velit.",
      "Quisque sed tellus malesuada, interdum tellus dapibus, dictum erat.",
      "Duis at enim placerat ligula volutpat semper.",
      "Phasellus accumsan lorem hendrerit, accumsan nunc vel, maximus sem.",
      "Nullam non ante id augue fermentum varius non vel nisi.",
      "Vestibulum vel tellus non orci tristique auctor.",
      "Morbi quis augue quis augue venenatis molestie ut a justo.",
      "Quisque vitae dolor sit amet nulla fringilla imperdiet lobortis vitae elit.",
      "Maecenas eget felis egestas, tempus enim quis, elementum neque.",
      "Duis tristique sem ac erat lacinia consectetur sit amet sed risus.",
      "Sed tincidunt risus pulvinar ligula elementum, eu elementum odio congue.",
      "Aenean at enim gravida, venenatis tellus eget, pellentesque nibh.",
      "Nunc tincidunt lacus nec purus mattis ultrices.",
      "Vivamus quis risus at leo blandit interdum eget sed lorem.",
      "Donec at magna at orci consequat dignissim.",
      "Phasellus vel nibh ut nunc dignissim ultrices sed eu magna.",
      "Mauris eget ex at diam facilisis hendrerit.",
      "Vestibulum consectetur nulla sed leo semper malesuada.",
      "Aliquam eleifend orci quis lectus euismod, et posuere erat viverra.",
      "Nullam gravida dui sit amet justo lacinia hendrerit.",
      "Maecenas vitae nisi finibus, ultricies ex finibus, rutrum lectus.",
      "Mauris tempus nisl id eleifend luctus.",
      "Quisque malesuada dolor sed arcu aliquet rutrum.",
      "Suspendisse volutpat leo eu consectetur iaculis.",
      "Duis ut erat bibendum massa tincidunt sollicitudin.",
      "Vivamus convallis nulla non odio efficitur, vitae porttitor ipsum aliquam.",
      "Sed pellentesque erat sed sapien venenatis mollis.",
      "Nam volutpat lacus ut justo consequat varius.",
      "Pellentesque non turpis sed metus blandit mattis eget interdum sapien.",
      "Integer ut ipsum eget purus hendrerit tincidunt.",
      "Etiam posuere ante convallis, fringilla sem varius, condimentum nulla.",
      "Ut mattis turpis eu finibus pellentesque.",
      "Nunc ultrices metus eu magna tempor tincidunt.",
      "Etiam viverra lectus ut turpis tempor consequat.",
      "Fusce fermentum ligula et elit imperdiet, nec vulputate nibh sodales.",
      "Nunc et tortor nec erat dapibus commodo nec id nibh.",
      "Praesent eget ligula et nibh pretium bibendum.",
      "Phasellus at eros at risus maximus consectetur.",
      "Fusce interdum lorem ullamcorper, malesuada nisl et, congue sem.",
      "Duis vel sapien porttitor, convallis est vitae, tincidunt ligula.",
      "Integer et purus tincidunt, lacinia magna a, dignissim libero.",
      "Quisque sed enim accumsan, pretium lacus at, venenatis mauris.",
      "Pellentesque pharetra nunc vitae nibh convallis consectetur.",
      "Nulla sit amet diam nec nibh suscipit lacinia.",
      "Curabitur cursus felis quis felis pharetra, vitae tempus sapien aliquet.",
      "Vestibulum vitae sapien non odio lacinia feugiat at vel sem.",
      "Etiam blandit tortor ac ex vestibulum blandit.",
      "Pellentesque quis purus placerat, tincidunt metus eget, fermentum neque.",
      "Morbi ac purus varius, faucibus sapien ut, tempor diam.",
      "Maecenas at mauris mattis, vestibulum felis ut, interdum dui.",
      "In mollis elit non sem lobortis commodo.",
      "In sit amet purus semper, ullamcorper nulla ut, semper libero.",
      "Nunc convallis risus mattis est maximus, in placerat nibh scelerisque.",
      "In auctor arcu a gravida hendrerit.",
      "Donec non odio id neque fringilla malesuada.",
      "Mauris eget libero sit amet massa ultrices placerat eget quis lectus.",
      "Maecenas at metus eu elit tempus lacinia.",
      "Sed elementum massa ac sapien euismod, iaculis aliquet libero tincidunt.",
      "Suspendisse consequat ante sed aliquet aliquam.",
      "Vestibulum ultricies ex sed velit faucibus, ac sagittis eros dignissim.",
      "Praesent elementum tellus in eros mattis feugiat.",
      "Nam gravida eros eu faucibus dignissim.",
      "Nullam sollicitudin nunc sit amet quam rhoncus sodales."
   };
}
