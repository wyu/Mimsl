package org.ms2ms.servlet;

import com.hfg.html.*;
import org.expasy.mzjava.core.ms.peaklist.Peak;
import org.expasy.mzjava.proteomics.ms.consensus.PeptideConsensusSpectrum;
import org.ms2ms.alg.Peaks;
import org.ms2ms.mimsl.MIMSL;
import org.ms2ms.mimsl.MimslSettings;
import org.ms2ms.mzjava.AnnotatedSpectrum;
import org.ms2ms.utils.Tools;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: wyu
 * Date: 5/7/14
 * Time: 12:05 AM
 * To change this template use File | Settings | File Templates.
 */
public class MimslServlet extends HttpServlet
{
  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
  {
    doGet(request, response);
  }

  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
  {
    // retrieve the parameters
    String insts=request.getParameterValues("instrument")[0],
             pmz=request.getParameterValues("precursor_mz")[0],
              pz=request.getParameterValues("pz")[0],
            frag=request.getParameterValues("fragments")[0];

    response.getWriter().println(lookup(Peaks.newPeaks(pmz, Integer.valueOf(pz)), MimslSettings.valueOf(insts),Peaks.newPeaks(frag, 1)));
/*

    PeakList<PepLibPeakAnnotation> ions = new DoublePeakList<PepLibPeakAnnotation>();
    Tolerance prec_tol = new PpmTolerance(15d), frag_tol = new AbsoluteTolerance(0.5d);
    byte[] spec_type = HBasePeakList.SPEC_TRAP_CID;
    try
    {
      if      (insts[0].equals(Peaks.OBT_HR_CID)) { frag_tol = new PpmTolerance(15d);}
      else if (insts[0].equals(Peaks.LTQ_CID))    { prec_tol = new AbsoluteTolerance(0.5d); }
      else if (insts[0].equals(Peaks.OBT_HCD))    { frag_tol = new PpmTolerance(15d); spec_type = HBasePeakList.SPEC_TRAP_HCD; }
      else if (insts[0].equals(Peaks.QTOF))       { prec_tol = new AbsoluteTolerance(0.15d); frag_tol = new AbsoluteTolerance(0.15d); spec_type = HBasePeakList.SPEC_QTOF; }

      ions.setPrecursor(new Peak(Double.valueOf(pmz[0]), 0, Integer.valueOf(pz[0])));
      String[] frags=frag[0].split(";|,|\\s");
      for (String f : frags)
        ions.add(Double.valueOf(f), 0d);

      // 500.730, +2(6): 318.20,520.19,568.30,683.25,782.32,869.35,
      List<AnnotatedSpectrum> candidates = MIMSL.run(ions, spec_type, prec_tol, frag_tol);

      HTML   html = new HTML();
      HTMLDoc doc = HTMLTags.newHTMLDoc(html);
      Table table = newCandidateTable(candidates);
      html.getBody().addSubtag(new H2("Candidate MS/MS Spectra"));
      html.getBody().addSubtag(table);
      html.getBody().addSubtag(new Div("Instrument: " + insts[0] + ", Precursor: " + pmz[0] + ", +" + pz[0] + "; Fragment Ions: " + frag[0]).setClass("cls_footnote"));
      response.getWriter().println(doc.toIndentedHTML(2,2));
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
*/
  }
  public static Table newCandidateTable(Collection<AnnotatedSpectrum> candidates)
  {
    if (!Tools.isSet(candidates)) return null;

    Table table = new Table(); table.setClass("cls_listing");

    Tr header = HTMLTags.newHeaderRow(table, "score","delta","vote","verdict","&nbsp;ppm","&nbsp;peptide","&nbsp;m/z","z","sig","mis","&nbsp;protein");
    header.setClass("cls_head_row");

    int rows=0;
    for (AnnotatedSpectrum candidate : candidates)
    {
      String[] peptide = candidate.getComment().split("\\^");
      boolean isnorm = candidate.getStatus().equals(PeptideConsensusSpectrum.Status.NORMAL);
      Tr row = HTMLTags.newRow(table,
          Tools.d2s(candidate.getScore(AnnotatedSpectrum.SCR_MIMSL),       2),
          Tools.d2s(candidate.getScore(AnnotatedSpectrum.SCR_MIMSL_DELTA), 2),
          candidate.getIonMatched()+"",
          !isnorm?candidate.getStatus().toString():"&nbsp;",
          isnorm?Tools.d2s(Peaks.toPPM(candidate.getPrecursor().getMz(), candidate.getMzQueried()), 2):"&nbsp;",
          peptide[0],
          Tools.d2s(candidate.getPrecursor().getMz(), 4),
          candidate.getPrecursor().getCharge()+"",
          candidate.getIonIndexed()+"",
          (candidate.getIonIndexed()-candidate.getIonMatched())+"",
          (peptide.length>1?peptide[1]+"^cls_nowrap":""));
      row.setClass(++rows%2==0 ? "cls_shade_row":"cls_light_row");
    }
    return table;
  }
  public static String lookup(Peak[] precursors, MimslSettings settings, Peak... frags)
  {
    try
    {
      List<AnnotatedSpectrum> candidates = MIMSL.run(precursors, settings, frags);

      HTML   html = new HTML();
      HTMLDoc doc = HTMLTags.newHTMLDoc(html, "MS/MS Spectra that are Share the Query Fragments and Precursor(s)");
      Table table = newCandidateTable(candidates);
      html.getBody().addSubtag(new H2("Candidate MS/MS Spectra"));
      html.getBody().addSubtag(table);
//      html.getBody().addSubtag(new Div("Instrument: " + insts[0] + ", Precursor: " + pmz[0] + ", +" + pz[0] + "; Fragment Ions: " + frag[0]).setClass("cls_footnote"));
      return doc.toIndentedHTML(2,2);
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
    return null;
  }
}
