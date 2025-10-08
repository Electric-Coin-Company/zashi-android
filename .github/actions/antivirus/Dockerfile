FROM clamav/clamav:1.5.0
RUN freshclam
ENTRYPOINT ["clamscan", "--recursive"]
